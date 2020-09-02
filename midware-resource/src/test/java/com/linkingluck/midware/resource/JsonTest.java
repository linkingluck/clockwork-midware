package com.linkingluck.midware.resource;

import com.linkingluck.midware.resource.bean.Storage;
import com.linkingluck.midware.resource.bean.StorageManager;
import com.linkingluck.midware.resource.json.ItemConfig;
import com.linkingluck.midware.utility.threadpool.IoThreadPool;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import static junit.framework.TestCase.assertEquals;

@Component
public class JsonTest {

	@Autowired
	private StorageManager storageManager;

	@Before
	public void startUp() {
		IoThreadPool.init(3);
	}


	@Test
	public void testGetResourceFromStorageManager() {

		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("jsonTestInTest.xml");

		JsonTest jsonTest = (JsonTest) applicationContext.getBean(JsonTest.class);

		Storage storage = jsonTest.storageManager.getStorage(ItemConfig.class);
		ItemConfig itemConfig = (ItemConfig) storage.get(1, false);

		assertEquals(1, itemConfig.getId());
	}

}
