package com.linkingluck.midware.network.tcp;

import com.alibaba.fastjson.JSON;
import com.linkingluck.midware.network.client.TcpClient;
import com.linkingluck.midware.network.core.TcpRequestPacket;
import com.linkingluck.midware.network.handler.SocketPacketHandler;
import com.linkingluck.midware.network.packet.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:TcpClientTest.xml"})
public class TcpClientTest extends AbstractJUnit4SpringContextTests {


	@Test
	public void testClientTcp() {
		SocketPacketHandler socketPacketHandler = applicationContext.getBean(SocketPacketHandler.class);

		TcpClient tcpClient = new TcpClient(new ChannelInboundHandlerAdapter() {

			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

				TcpRequestPacket packet = (TcpRequestPacket) msg;

				LoginAuthResp loginAuthResp = (LoginAuthResp) socketPacketHandler.decodePacket(packet.getPacketId(), packet.getData());
				System.out.println("client receive:" + loginAuthResp.getLoginTime() + ",pid:" + loginAuthResp.getPid() + ",account:" + loginAuthResp.getAccount());
			}
		});
		ChannelFuture channelFuture = tcpClient.connect("192.168.1.102", 4010);
		Channel channel = channelFuture.channel();
		ChannelFuture future = channel.writeAndFlush(socketPacketHandler.encodePacket(LoginAuthReq.valueOf("ted", 1)));
		future.addListener(future1 -> System.out.println("client send success!!!!!"));
		try {
			TimeUnit.SECONDS.sleep(25);
			channel.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testDebugProtocolInfo() {
		SocketPacketHandler socketPacketHandler = applicationContext.getBean(SocketPacketHandler.class);

		TcpClient tcpClient = new TcpClient(new ChannelInboundHandlerAdapter() {

			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

				TcpRequestPacket packet = (TcpRequestPacket) msg;

				DebugProtocolInfoResp resp = (DebugProtocolInfoResp) socketPacketHandler.decodePacket(packet.getPacketId(), packet.getData());
				System.out.println("client receive:" + JSON.toJSONString(resp));
			}
		});
		ChannelFuture channelFuture = tcpClient.connect("192.168.12.52", 4010);
		Channel channel = channelFuture.channel();
		DebugProtocolInfoReq req = new DebugProtocolInfoReq();
		req.setPacketId(10001);
		ChannelFuture future = channel.writeAndFlush(socketPacketHandler.encodePacket(req));
		future.addListener(future1 -> System.out.println("client send success!!!!!"));
		try {
			TimeUnit.SECONDS.sleep(25);
			channel.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testDebugProtocol() {
		SocketPacketHandler socketPacketHandler = applicationContext.getBean(SocketPacketHandler.class);

		TcpClient tcpClient = new TcpClient(new ChannelInboundHandlerAdapter() {

			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

			}
		});
		ChannelFuture channelFuture = tcpClient.connect("192.168.12.52", 4010);
		Channel channel = channelFuture.channel();
		DebugProtocolReq req = new DebugProtocolReq();
		req.setPacketId(10103);

//		{"account":"ted","pid":"1","platform":"2"}
		req.setContext("tes");
		ChannelFuture future = channel.writeAndFlush(socketPacketHandler.encodePacket(req));
		future.addListener(future1 -> System.out.println("client send success!!!!!"));
		try {
			TimeUnit.SECONDS.sleep(25);
			channel.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}



