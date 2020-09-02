package com.linkingluck.midware.resource;

import com.linkingluck.midware.utility.threadpool.IoThreadPool;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringSchemaTest {

	@Before
	public void startUp() {
		IoThreadPool.init(3);
	}

	@Test
	public void test() {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:springSchemaInTest.xml");
	}
}
