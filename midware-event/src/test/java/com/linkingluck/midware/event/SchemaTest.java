package com.linkingluck.midware.event;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SchemaTest {

	@Test
	public void testSchema() {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("schemaTest.xml");
	}
}
