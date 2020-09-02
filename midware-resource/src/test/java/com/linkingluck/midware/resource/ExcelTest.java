package com.linkingluck.midware.resource;

import com.linkingluck.midware.resource.bean.Storage;
import com.linkingluck.midware.resource.bean.StorageManager;
import com.linkingluck.midware.resource.excel.RandomNameResource;
import com.linkingluck.midware.utility.threadpool.IoThreadPool;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import static junit.framework.TestCase.assertEquals;

@Component
public class ExcelTest {

	@Autowired
	private StorageManager storageManager;

	@Before
	public void startUp() {
		IoThreadPool.init(3);
	}


	@Test
	public void testGetResourceFromStorageManager() {

		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:springSchemaInTest.xml");

		ExcelTest excelTest = (ExcelTest) applicationContext.getBean(ExcelTest.class);

		Storage storage = excelTest.storageManager.getStorage(RandomNameResource.class);
		RandomNameResource randomNameResource = (RandomNameResource) storage.get(1, false);

		assertEquals(1, randomNameResource.getId());
	}

}
