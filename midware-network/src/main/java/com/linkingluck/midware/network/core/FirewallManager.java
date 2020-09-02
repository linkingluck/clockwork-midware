package com.linkingluck.midware.network.core;

public interface FirewallManager {

	boolean packetFilter(TcpSession session, TcpRequestPacket packet);

}
