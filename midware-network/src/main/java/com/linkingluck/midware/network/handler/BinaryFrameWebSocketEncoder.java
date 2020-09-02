package com.linkingluck.midware.network.handler;

import com.linkingluck.midware.network.core.TcpResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

public class BinaryFrameWebSocketEncoder extends MessageToMessageEncoder<TcpResponsePacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, TcpResponsePacket msg, List<Object> out) throws Exception {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        msg.write(buf);
        WebSocketFrame frame = new BinaryWebSocketFrame(buf);
        out.add(frame);
    }
}
