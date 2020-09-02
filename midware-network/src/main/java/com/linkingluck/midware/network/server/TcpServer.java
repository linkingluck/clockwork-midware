package com.linkingluck.midware.network.server;

import com.linkingluck.midware.network.codec.TcpPacketDecoder;
import com.linkingluck.midware.network.codec.TcpPacketEncoder;
import com.linkingluck.midware.network.config.ServerConfig;
import com.linkingluck.midware.network.handler.CustomHandlerManager;
import com.linkingluck.midware.network.handler.SessionHandler;
import com.linkingluck.midware.network.handler.SocketPacketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;


@Component
public class TcpServer {

	private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);

	@Autowired
	private SessionHandler sessionHandler;

	@Autowired
	private SocketPacketHandler socketPacketHandler;

	@Autowired
	private CustomHandlerManager customHandlerManager;

	private LoggingHandler loggingHandler;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workGroup;
	private List<ChannelFuture> channelFutures = new ArrayList<>();

	private String host;
	private int[] ports;
	private int maxLength;

	public void loadProperties(Resource resource) throws IOException {
		Properties properties = new Properties();
		properties.load(resource.getInputStream());
		this.host = properties.getProperty(ServerConfig.HOST);
		IntStream intStream = Arrays.stream(StringUtils.split(properties.getProperty(ServerConfig.PORTS), ServerConfig.SPLIT)).mapToInt(s -> Integer.parseInt(s));
		this.ports = intStream.toArray();
		String maxLengthProp = properties.getProperty(ServerConfig.PACKET_MAXLENGTH);
		this.maxLength = 1024 * 1024; //1M
		if (maxLengthProp != null) {
			this.maxLength = Integer.parseInt(maxLengthProp) * 1024;
		}
	}

	public void bind() throws InterruptedException {
		this.bossGroup = new NioEventLoopGroup();
		this.workGroup = new NioEventLoopGroup();

		ServerBootstrap bootstrap = new ServerBootstrap();

		if (loggingHandler != null) {
			bootstrap.handler(loggingHandler);
		}

		bootstrap.group(bossGroup, workGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
				.option(ChannelOption.SO_BACKLOG, 1024)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childOption(ChannelOption.SO_RCVBUF, 1024 * 32)
				.childOption(ChannelOption.SO_SNDBUF, 1024 * 32)
				.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark.DEFAULT)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast("session", sessionHandler);
						ch.pipeline().addLast("decoder", new TcpPacketDecoder(maxLength));
						ch.pipeline().addLast("socketPacketHandler", socketPacketHandler);
						ch.pipeline().addLast("encoder", new TcpPacketEncoder());
						customHandlerManager.getHandlers().forEach(ch.pipeline()::addLast);
					}
				});

		for (int port : ports) {
			ChannelFuture cf = null;
			if (StringUtils.isNotEmpty(host)) {
				cf = bootstrap.bind(host, port);
			} else {
				cf = bootstrap.bind(port);
			}
			cf.sync();
			channelFutures.add(cf);
		}
	}

	public void shutdownGracefully() {
		try {
			for (ChannelFuture cf : channelFutures) {
				if (cf.channel() == null) {
					continue;
				}
				cf.channel().close();
			}
		} finally {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}

	public LoggingHandler getLoggingHandler() {
		return loggingHandler;
	}

	public void setLoggingHandler(LoggingHandler loggingHandler) {
		this.loggingHandler = loggingHandler;
	}

	public int[] getPorts() {
		return ports;
	}

	public List<ChannelFuture> getChannelFutures() {
		return channelFutures;
	}
}
