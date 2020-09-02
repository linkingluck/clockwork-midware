package com.linkingluck.midware.network.handler;


import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufIDLGenerator;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.linkingluck.midware.event.core.EventBusManager;
import com.linkingluck.midware.event.core.IEventCallBack;
import com.linkingluck.midware.network.anno.SocketPacket;
import com.linkingluck.midware.network.core.*;
import com.linkingluck.midware.network.server.TcpServer;
import com.linkingluck.midware.network.utils.IpUtils;
import com.linkingluck.midware.utility.FileUtils;
import com.linkingluck.midware.utility.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Sharable
@Component
public class SocketPacketHandler extends ChannelInboundHandlerAdapter implements BeanPostProcessor, ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(SocketPacketHandler.class);

	@Autowired
	private SessionManager sessionManager;

	private FirewallManager firewallManager;

	private static SocketPacketHandler instance;


	@PostConstruct
	public void init() {
		instance = this;
	}

	public static SocketPacketHandler getInstance() {
		return instance;
	}

	/**
	 * 类class与packetId的快速映射
	 */
	private Map<Class<?>, Integer> packetClass2PacketId = new ConcurrentHashMap<>();

	private Map<Integer, Class<?>> packetId2PacketClass = new ConcurrentHashMap<>();

	private Map<Integer, Codec> packetIdClass2Codec = new ConcurrentHashMap<>();


	@Override
	public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
		Class<?> clazz = bean.getClass();
		if (!clazz.isAnnotationPresent(SocketPacket.class)) {
			return bean;
		}

		SocketPacket anno = clazz.getAnnotation(SocketPacket.class);
		int packetId = anno.packetId();
		if (packetId2PacketClass.put(packetId, clazz) != null) {
			throw new RuntimeException(MessageFormat.format("类[{0}]的packetId[{1}]协议号重复", clazz.getSimpleName(), packetId));
		}
		packetClass2PacketId.put(clazz, packetId);
		Codec<?> codec = ProtobufProxy.create(clazz);
		packetIdClass2Codec.put(packetId, codec);

		return bean;
	}

	private void iDLGenerator(Class<?> clazz, short packetId) throws Exception {
		String idl = ProtobufIDLGenerator.getIDL(clazz, null, null, true);
		// todo 配置路径
		File file = new File("D:\\proto\\" + packetId + "_" + clazz.getSimpleName() + ".proto");
		FileUtils.createFile(file);
		FileWriter fw = new FileWriter(file);
		fw.write(idl);
		fw.flush();
		fw.close();
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
		return bean;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(!(msg instanceof TcpRequestPacket)) {
			return;
		}

		final TcpRequestPacket packet = (TcpRequestPacket) msg;
		final Channel channel = ctx.channel();

		final TcpSession session = sessionManager.getSession(ctx.channel().id());

		if (firewallManager != null && !firewallManager.packetFilter(session, packet)) {
			logger.warn(String.format("session[%s]发送非法的消息packetId[%s]!", IpUtils.getIp(ctx.channel().localAddress().toString())),
					packet.getPacketId());
		}

		//解码消息
		Object message = decodePacket(packet.getPacketId(), packet.getData());

		//打印上行协议消息
		if(logger.isDebugEnabled()) {
			logger.info("-->>{}:{}", message.getClass().getSimpleName(), JsonUtils.object2String(message));
		}

		//分发消息到业务模块处理
		excMessage(session, packet.getPacketId(), message);
	}

	public Object decodePacket(short packetId, byte[] data) {
		Codec<?> codec = getCodec(packetId);
		if (codec == null) {
			logger.error("{} not have codec", packetId);
			return null;
		}

		try {
			return codec.decode(data);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(MessageFormat.format("decode packet[{0}] error", packetId), e);
		}
	}

	private Codec getCodec(int packetId) {
		return packetIdClass2Codec.get(packetId);
	}

	public void excMessage(TcpSession session, short packetId, Object message) {
		if (message == null) {
			return;
		}

		IEventCallBack eventCallBack = buildEventCallback(session);
		//todo 不应在nio线程处理 应该异步处理
		EventBusManager.getInstance().syncSubmitWithContext(message, eventCallBack, session);
	}

	private IEventCallBack buildEventCallback(TcpSession session) {
		return new IEventCallBack() {
			@Override
			public void callback(Object returnValue) {
				if (returnValue != null) {
					session.sendPacket(returnValue, false);
				}
			}

			@Override
			public void exception(Throwable throwable) {

			}
		};
	}

	public ChannelFuture sendPacket(TcpSession session, Object message, boolean flush) {
		TcpResponsePacket packet = encodePacket(message);
		return session.sendPacket(packet, flush);
	}

	public TcpResponsePacket encodePacket(Object message) {
		if (message instanceof TcpResponsePacket) {
			return (TcpResponsePacket) message;
		}

		Integer packetId = packetClass2PacketId.get(message.getClass());
		if (packetId == null) {
			throw new RuntimeException(MessageFormat.format("not found packetId[{0}] in packetClass2PacketId", packetId));
		}
		byte[] bytes = new byte[0];
		try {
			bytes = getCodec(packetId).encode(message);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(MessageFormat.format("packetId[{0}] encode error", packetId));
		}

		//打印下行协议消息
		if(logger.isDebugEnabled()) {
			try {
				String text = JsonUtils.object2String(getCodec(packetId).decode(bytes));
				logger.info("<<--{}:{}", message.getClass().getSimpleName(), text);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(MessageFormat.format("decode packet[{0}] error", packetId), e);
			}
		}

		TcpResponsePacket packet = TcpResponsePacket.valueOf(packetId.shortValue(), bytes);
		return packet;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("通信包异常!", cause);
		cause.printStackTrace();
		ctx.close();
	}

	public FirewallManager getFirewallManager() {
		return firewallManager;
	}

	public void setFirewallManager(FirewallManager firewallManager) {
		this.firewallManager = firewallManager;
	}

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public Map<Integer, Class<?>> getPacketId2PacketClass() {
		return packetId2PacketClass;
	}
}
