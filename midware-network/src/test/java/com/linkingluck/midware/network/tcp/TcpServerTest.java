package com.linkingluck.midware.network.tcp;

import com.linkingluck.midware.event.anno.EventReceiver;
import com.linkingluck.midware.network.core.TcpSession;
import com.linkingluck.midware.network.handler.SocketPacketHandler;
import com.linkingluck.midware.network.packet.LoginAuthReq;
import com.linkingluck.midware.network.packet.LoginAuthResp;
import com.linkingluck.midware.network.server.TcpServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:TcpServerTest.xml"})
public class TcpServerTest extends AbstractJUnit4SpringContextTests {

	@EventReceiver
	public void LoginAuthReqHandler(LoginAuthReq loginAuthReq, TcpSession session) {
		SocketPacketHandler socketPacketHandler = applicationContext.getBean(SocketPacketHandler.class);

		//分发到此进行业务处理
		String account = loginAuthReq.getAccount();
		System.out.println("server receive in LoginAuthReqHandler:" + account);

		socketPacketHandler.sendPacket(session, LoginAuthResp.valueOf(account, (int) (System.currentTimeMillis() / 1000)), true);

//		TcpServer tcpServer = applicationContext.getBean(TcpServer.class);
//		tcpServer.shutdownGracefully();
	}

	@Test
	public void testServerTcp() {
		TcpServer tcpServer = applicationContext.getBean(TcpServer.class);
		DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
		Resource resource = defaultResourceLoader.getResource("wnet.properties");
		try {
			tcpServer.loadProperties(resource);
			tcpServer.bind();
			tcpServer.getChannelFutures().forEach(channelFuture -> {
				try {
					channelFuture.channel().closeFuture().sync();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}



