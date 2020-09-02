package com.linkingluck.midward.agent.hotupdate;

/**
 * Created by linkingluck
 */
public class HotUpdateClassLoader extends ClassLoader {

	protected Class<?> findClass(byte[] bytecode) {
		return this.defineClass(null, bytecode, 0, bytecode.length);
	}

}
