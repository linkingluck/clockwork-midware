package com.linkingluck.midware.event;

import com.linkingluck.midware.event.anno.EventReceiver;
import com.linkingluck.midware.event.core.IEventBusManager;
import com.linkingluck.midware.event.core.IEventCallBack;
import com.linkingluck.midware.event.event.PlayerLoginTimeEvent;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EventCallBackTest {

	@EventReceiver
	public String playerLoginTimeEventHandler(PlayerLoginTimeEvent event, int arg1, String arg2) {
		System.out.println(event.getTimestamp());
		System.out.println(arg1);
		System.out.println(arg2);
		return "playerLoginTimeEventHandler";
	}

	@Test
	public void playerLoginTimeEventSubmitTest() {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("EventCallBackTest.xml");
		IEventBusManager eventBusManager = applicationContext.getBean(IEventBusManager.class);
		eventBusManager.syncSubmitWithContext(PlayerLoginTimeEvent.valueOf((int) (System.currentTimeMillis() / 1000L)), new IEventCallBack() {
			@Override
			public void callback(Object returnValue) {
				System.out.println(returnValue);
			}

			@Override
			public void exception(Throwable throwable) {

			}
		}, 1, "hello2");
	}


}
