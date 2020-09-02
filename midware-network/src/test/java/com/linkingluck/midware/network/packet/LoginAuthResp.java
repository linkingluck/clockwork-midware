package com.linkingluck.midware.network.packet;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.linkingluck.midware.network.anno.SocketPacket;

@SocketPacket(packetId = PacketId.LOGIN_AUTH_RESP)
public class LoginAuthResp {

	@Protobuf(description = "账号")
	private String account;

	@Protobuf(description = "登陆时间")
	private int loginTime;

	private int pid;

	public static LoginAuthResp valueOf(String account, int loginTime) {
		LoginAuthResp vo = new LoginAuthResp();
		vo.account = account;
		vo.loginTime = loginTime;
		vo.pid = 9;
		return vo;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public int getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(int loginTime) {
		this.loginTime = loginTime;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}


}
