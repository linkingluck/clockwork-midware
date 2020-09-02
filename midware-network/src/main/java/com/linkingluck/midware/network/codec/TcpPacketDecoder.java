package com.linkingluck.midware.network.codec;

import com.linkingluck.midware.network.core.TcpRequestPacket;
import com.linkingluck.midware.network.utils.IpUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 可变长帧数据
 * |size-int-4|packetId-short-2|data|
 */
public class TcpPacketDecoder extends ByteToMessageDecoder {

	private Logger logger = LoggerFactory.getLogger(TcpPacketDecoder.class);

	/**
	 * 1m
	 */
	private static int MAX_SIZE = 1 * 1024 * 1024;

	private static int MIN_SIZE = 2;

	/**
	 * length+packetId = 6
	 */
	private static final int MIN_READABLE = 4 + 2;

	public TcpPacketDecoder(int maxLength) {
		if (maxLength <= MIN_SIZE) {
			throw new RuntimeException(String.format("maxLength error! length[%s] MIN_SIZE[%s]", maxLength, MIN_SIZE));
		}
		MAX_SIZE = maxLength;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.refCnt() == 0) {
			return;
		}

		in.markReaderIndex();
		int readableBytes = in.readableBytes();
		if (readableBytes < MIN_READABLE) {
			return;
		}

		int size = in.readInt();
		if (size <= MIN_SIZE) {
			// 包长度不够
			logger.warn(String.format("ip[%s] send size error.config_minsize[%s] size[%s]",
					IpUtils.getIp(ctx.channel().remoteAddress().toString()), MIN_SIZE, size));
			ctx.close();
		}

		if (size >= MAX_SIZE) {
			// 超过最大长度
			logger.warn(String.format("ip[%s] send size error.config_maxsize[%s] size[%s]",
					IpUtils.getIp(ctx.channel().remoteAddress().toString()), MAX_SIZE, size));
			ctx.close();
		}
		if (in.readableBytes() < size) {
			in.resetReaderIndex();
			return;
		}

		short packetId = in.readShort();

		byte[] data = new byte[size - 2];
		in.readBytes(data);

		TcpRequestPacket requestPacket = TcpRequestPacket.valueOf(packetId, data);
		out.add(requestPacket);
	}

}
