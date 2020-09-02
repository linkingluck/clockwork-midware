package com.linkingluck.midward.agent.hotupdate;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

/**
 * Created by lingkingluck
 */
public class HotUpdateAgent {

	public static void agentmain(String agentArgs, Instrumentation inst) throws IOException, ClassNotFoundException, UnmodifiableClassException {
		File file = new File(agentArgs);
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		byte[] byteCode = new byte[(int) file.length()];
		in.readFully(byteCode);
		in.close();

		HotUpdateClassLoader myLoader = new HotUpdateClassLoader();
		Class hotUpdateClass = myLoader.findClass(byteCode);
		System.err.println(hotUpdateClass.getName());
		Class<?> clz = Class.forName(hotUpdateClass.getName());
		ClassDefinition classDefinition = new ClassDefinition(clz, byteCode);
		inst.redefineClasses(classDefinition);
		System.err.println("redefine class" + agentArgs + " success");
	}
}
