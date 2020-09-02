package com.linkingluck.midware.network.config;

public class ServerConfig {

	/**
	 * 分隔符定义
	 */
	public static final String SPLIT = ",";

	/**
	 * 服务器的地址与端口配置，允许通过分隔符","指定多个地址
	 */
	public static final String HOST = "server.socket.host";

	/**
	 * RPC服务端口
	 */
	public static final String PORTS = "server.socket.port";

	/**
	 * 服务器接收最大包体长度
	 */
	public static final String PACKET_MAXLENGTH = "server.socket.maxlength";

	/**
	 * 必须的配置键
	 */
	public static final String[] KEYS = {HOST, PORTS};

}
