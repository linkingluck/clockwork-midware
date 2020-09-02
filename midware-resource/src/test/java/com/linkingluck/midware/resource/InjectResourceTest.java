package com.linkingluck.midware.resource;

import com.linkingluck.midware.resource.anno.ResourceInject;
import com.linkingluck.midware.resource.bean.Storage;
import com.linkingluck.midware.resource.excel.MapResource;
import com.linkingluck.midware.resource.excel.RandomNameResource;
import com.linkingluck.midware.utility.threadpool.IoThreadPool;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import static junit.framework.TestCase.assertEquals;

@Component
public class InjectResourceTest {

	@ResourceInject
	private Storage<Integer, RandomNameResource> storages;

	@ResourceInject
	private Storage<Integer, MapResource> mapResourceStorage;

	@Before
	public void startUp() {
		IoThreadPool.init(3);
	}

	@Test
	public void testInjectResource() {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:springSchemaInTest.xml");

		InjectResourceTest injectResourceTest = applicationContext.getBean(InjectResourceTest.class);
		RandomNameResource randomNameResource = injectResourceTest.storages.get(1, false);

		assertEquals(1, randomNameResource.getId());
		assertEquals("赵", randomNameResource.getFirstName());

		MapResource mapResource = injectResourceTest.mapResourceStorage.get(1, false);
		System.out.println(mapResource);
	}

}
