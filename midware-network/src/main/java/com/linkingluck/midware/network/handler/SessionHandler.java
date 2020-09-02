package com.linkingluck.midware.network.handler;

import com.linkingluck.midware.network.core.SessionManager;
import com.linkingluck.midware.network.core.TcpSession;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Sharable
@Component
public class SessionHandler extends ChannelInboundHandlerAdapter {

	@Autowired
	private SessionManager sessionManager;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		TcpSession session = TcpSession.valueOf(ctx.channel());
		sessionManager.add(session);
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		sessionManager.remove(ctx.channel().id());
	}

}