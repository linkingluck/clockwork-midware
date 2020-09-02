package com.linkingluck.midware.event.event;

public class PlayerExitTimeEvent {

	private String playerName;

	private int timestamp;

	public static PlayerExitTimeEvent valueOf(String playerName, int timestamp) {
		PlayerExitTimeEvent vo = new PlayerExitTimeEvent();
		vo.playerName = playerName;
		vo.timestamp = timestamp;
		return vo;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
}
