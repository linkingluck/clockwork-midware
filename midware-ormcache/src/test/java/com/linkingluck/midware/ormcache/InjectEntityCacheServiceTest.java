package com.linkingluck.midware.ormcache;

import com.linkingluck.midware.ormcache.anno.Inject;
import com.linkingluck.midware.ormcache.entity.Account;
import com.linkingluck.midware.ormcache.service.EntityCacheService;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class InjectEntityCacheServiceTest {

	@Inject
	private EntityCacheService<Long, Account> entityCacheService;

	@Test
	public void injectTest() throws InterruptedException {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:injectEntityCacheServiceInTest.xml");
		InjectEntityCacheServiceTest injectEntityCacheServiceTest = applicationContext.getBean(InjectEntityCacheServiceTest.class);
		EntityCacheService<Long, Account> cacheService = injectEntityCacheServiceTest.entityCacheService;
		cacheService.load(1L);
		Account test = cacheService.loadOrCreate(1L, k -> {
			Account account = Account.valueOf(k);
			account.setAccount("ted");
			account.setPlatCode("ios");
			account.setPid("360");
			return account;
		});
		TimeUnit.SECONDS.sleep(20);
		test = cacheService.load(1L);
		test.setPid("wechat");
		cacheService.writeBack(1L, test);
		TimeUnit.SECONDS.sleep(20);
	}
}
