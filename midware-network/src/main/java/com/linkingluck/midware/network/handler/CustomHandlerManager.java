package com.linkingluck.midware.network.handler;

import io.netty.channel.ChannelHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public final class CustomHandlerManager {

	private List<ChannelHandler> handlers = new ArrayList<>();

	public List<ChannelHandler> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<ChannelHandler> handlers) {
		this.handlers = handlers;
	}

	public void addHandlers(ChannelHandler channelHandler) {
		this.handlers.add(channelHandler);
	}
}
