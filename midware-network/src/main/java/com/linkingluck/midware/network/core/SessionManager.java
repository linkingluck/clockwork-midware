package com.linkingluck.midware.network.core;

import io.netty.channel.ChannelId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
	private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

	/**
	 * 所有会话
	 */
	private ConcurrentHashMap<ChannelId, TcpSession> allSessions = new ConcurrentHashMap<ChannelId, TcpSession>();

	public void add(TcpSession session) {
		if (allSessions.put(session.getChannel().id(), session) != null) {
			logger.error(String.format("channeld[%s]重复注册sessionManager,from ip[%s]", session.getChannel().id().asShortText(),
					session.getChannel().remoteAddress()));
		}
	}

	public int ipSessionCount(String ip) {
		int count = 0;
		for (TcpSession session : allSessions.values()) {
			if (session.getChannel().remoteAddress().toString().contains(ip)) {
				count++;
			}
		}
		return count;
	}

	public TcpSession getSession(ChannelId channelId) {
		return allSessions.get(channelId);
	}

	public void remove(ChannelId id) {
		allSessions.remove(id);
	}

}
