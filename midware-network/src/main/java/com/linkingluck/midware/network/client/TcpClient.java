package com.linkingluck.midware.network.client;

import com.linkingluck.midware.network.codec.TcpPacketDecoder;
import com.linkingluck.midware.network.codec.TcpPacketEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpClient {

	private static final Logger logger = LoggerFactory.getLogger(TcpClient.class);

	/**
	 * 所有client公用
	 **/
	private static EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

	private Bootstrap bootstrap = new Bootstrap();

	public TcpClient(ChannelHandler... handlers) {
		bootstrap.group(eventLoopGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast("decoder", new TcpPacketDecoder(10 * 1024 * 1024));
						ch.pipeline().addLast("encoder", new TcpPacketEncoder());
						ch.pipeline().addLast(handlers);
					}
				});
	}

	public ChannelFuture connect(String host, int port) {
		return bootstrap.connect(host, port);
	}

	public static void shutdownGracefully() {
		eventLoopGroup.shutdownGracefully();
	}

}
