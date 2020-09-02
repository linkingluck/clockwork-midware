package com.linkingluck.midware.network.codec;

import com.linkingluck.midware.network.core.TcpResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TcpPacketEncoder extends MessageToByteEncoder<TcpResponsePacket> {

	@Override
	protected void encode(ChannelHandlerContext ctx, TcpResponsePacket msg, ByteBuf out) {
		// length
		out.writeInt(msg.getData().length + 2);
		//packetId
		out.writeShort(msg.getPacketId());
		//data
		out.writeBytes(msg.getData());
	}

}
