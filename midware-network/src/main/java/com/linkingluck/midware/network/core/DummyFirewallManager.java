package com.linkingluck.midware.network.core;


public class DummyFirewallManager implements FirewallManager {

	@Override
	public boolean packetFilter(TcpSession session, TcpRequestPacket packet) {
		return false;
	}
}
