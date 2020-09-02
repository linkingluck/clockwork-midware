package com.linkingluck.midware.network.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.linkingluck.midware.network.anno.SocketPacket;

@SocketPacket(packetId = PacketId.DEBUG_PROTOCOL_INFO_RESP)
public class DebugProtocolInfoResp {

    @Protobuf(description = "协议id")
    private int packetId;

    @Protobuf(description = "协议数据")
    private String context;

    public static DebugProtocolInfoResp valueOf(int packetId, String content) {
        DebugProtocolInfoResp resp = new DebugProtocolInfoResp();
        resp.packetId = packetId;
        resp.context = content;
        return resp;
    }

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
