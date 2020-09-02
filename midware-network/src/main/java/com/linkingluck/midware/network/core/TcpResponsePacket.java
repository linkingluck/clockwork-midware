package com.linkingluck.midware.network.core;

import io.netty.buffer.ByteBuf;

public class TcpResponsePacket {
	private short packetId;
	private byte[] data;

	public static TcpResponsePacket valueOf(short packetId, byte[] data) {
		TcpResponsePacket wp = new TcpResponsePacket();
		wp.setPacketId(packetId);
		wp.data = data;
		return wp;
	}

	public short getPacketId() {
		return packetId;
	}

	public void setPacketId(short packetId) {
		this.packetId = packetId;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public void write(ByteBuf buf) {
		//length
		buf.writeInt(getData().length + 2);
		//packetId
		buf.writeShort(getPacketId());
		//data
		buf.writeBytes(getData());
	}
}
