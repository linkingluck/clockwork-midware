package com.linkingluck.midware.network.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.linkingluck.midware.network.anno.SocketPacket;

@SocketPacket(packetId = PacketId.DEBUG_PROTOCOL_INFO_REQ)
public class DebugProtocolInfoReq {

    @Protobuf(description = "协议id")
    private int packetId;

    public int getPacketId() {
        return packetId;
    }

    public void setPacketId(int packetId) {
        this.packetId = packetId;
    }
}
