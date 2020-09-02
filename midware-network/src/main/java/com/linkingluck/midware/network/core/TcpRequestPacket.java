package com.linkingluck.midware.network.core;

public class TcpRequestPacket {

	private short packetId;
	private byte[] data;

	public static TcpRequestPacket valueOf(short packetId, byte[] data) {
		TcpRequestPacket wp = new TcpRequestPacket();
		wp.setPacketId(packetId);
		wp.data = data;
		return wp;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public short getPacketId() {
		return packetId;
	}

	public void setPacketId(short packetId) {
		this.packetId = packetId;
	}

}
