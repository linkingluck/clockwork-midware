package com.linkingluck.midware.network.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.linkingluck.midware.network.anno.SocketPacket;

@SocketPacket(packetId = PacketId.LOGIN_AUTH_REQ)
public class LoginAuthReq {

	@Protobuf(description = "账号")
	private String account;

	@Protobuf(description = "平台")
	private int platform;

	public static LoginAuthReq valueOf(String account, int platform) {
		LoginAuthReq vo = new LoginAuthReq();
		vo.account = account;
		vo.platform = platform;
		return vo;
	}

	private int pid;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public int getPlatform() {
		return platform;
	}

	public void setPlatform(int platform) {
		this.platform = platform;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}
}
