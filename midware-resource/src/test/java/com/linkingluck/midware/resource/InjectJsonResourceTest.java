package com.linkingluck.midware.resource;

import com.linkingluck.midware.resource.anno.ResourceInject;
import com.linkingluck.midware.resource.bean.Storage;
import com.linkingluck.midware.resource.json.ItemConfig;
import com.linkingluck.midware.utility.threadpool.IoThreadPool;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import static junit.framework.TestCase.assertEquals;

@Component
public class InjectJsonResourceTest {

	@ResourceInject
	private Storage<Integer, ItemConfig> storages;

	@Before
	public void startUp() {
		IoThreadPool.init(3);
	}

	@Test
	public void testInjectResource() {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("jsonTestInTest.xml");

		InjectJsonResourceTest injectJsonResourceTest = applicationContext.getBean(InjectJsonResourceTest.class);
		ItemConfig itemConfig = injectJsonResourceTest.storages.get(1, false);

		assertEquals(1, itemConfig.getId());

		System.out.println(itemConfig);
	}

}
