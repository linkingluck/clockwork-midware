package com.linkingluck.midware.network.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.linkingluck.midware.network.anno.SocketPacket;

@SocketPacket(packetId = PacketId.DEBUG_PROTOCOL_REQ)
public class DebugProtocolReq {

    @Protobuf(description = "协议id")
    private int packetId;

    @Protobuf(description = "协议数据")
    private String context;

    public int getPacketId() {
        return packetId;
    }

    public void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
