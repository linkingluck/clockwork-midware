package com.linkingluck.midware.event.event;

public class PlayerLoginTimeEvent {

	private int timestamp;

	public static PlayerLoginTimeEvent valueOf(int timestamp) {
		PlayerLoginTimeEvent vo = new PlayerLoginTimeEvent();
		vo.timestamp = timestamp;
		return vo;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}
}
