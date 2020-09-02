package com.linkingluck.midware.network.core;

import com.linkingluck.midware.network.handler.SocketPacketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class TcpSession {

	private static final AtomicInteger SEQ = new AtomicInteger(1);

	private int id;

	private Channel channel;

	private int dispatcherHashCode;

	private Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

	public static TcpSession valueOf(Channel channel) {
		TcpSession session = new TcpSession();
		session.channel = channel;
		session.id = SEQ.incrementAndGet();
		return session;
	}

	private TcpSession() {
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int selectDispatcherHashCode() {
		if (dispatcherHashCode == 0) {
			return Math.abs(channel.hashCode());
		}
		return dispatcherHashCode;
	}

	public int getDispatcherHashCode() {
		return dispatcherHashCode;
	}

	public void setDispatcherHashCode(int dispatcherHashCode) {
		this.dispatcherHashCode = dispatcherHashCode;
	}

	public ChannelFuture sendPacket(Object packet, boolean flush) {
		TcpResponsePacket resp = SocketPacketHandler.getInstance().encodePacket(packet);
		if (flush) {
			return channel.writeAndFlush(resp);
		}
		return channel.write(resp);
	}

	public ChannelFuture sendPacket(Object packet) {
		return sendPacket(packet, false);
	}
}
