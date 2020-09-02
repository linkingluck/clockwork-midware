package com.linkingluck.midware.event;

import com.linkingluck.midware.event.anno.EventReceiver;
import com.linkingluck.midware.event.core.IEventBusManager;
import com.linkingluck.midware.event.event.PlayerLoginTimeEvent;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EventReceiverTest {

	@EventReceiver
	public void playerLoginTimeEventHandler1(PlayerLoginTimeEvent event) {
		System.out.println(event.getTimestamp());
	}

	@EventReceiver
	public void playerLoginTimeEventHandler2(PlayerLoginTimeEvent event, int arg1, String arg2) {
		System.out.println(event.getTimestamp());
		System.out.println(arg1);
		System.out.println(arg2);
	}

	@EventReceiver
	public void playerLoginTimeEventHandler3(PlayerLoginTimeEvent event, long arg1) {
		System.out.println(event.getTimestamp());
		System.out.println(arg1);
	}

	@Test
	public void playerLoginTimeEventSubmitTest() {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("eventReceiverTest.xml");

//		EventReceiverTest eventReceiverTest = applicationContext.getBean(EventReceiverTest.class);

		IEventBusManager eventBusManager = applicationContext.getBean(IEventBusManager.class);
		PlayerLoginTimeEvent event = PlayerLoginTimeEvent.valueOf((int) (System.currentTimeMillis() / 1000L));
		eventBusManager.syncSubmit(event);
	}

	@Test
	public void playerLoginTimeEventSubmitWithContextTest() {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("eventReceiverTest.xml");

//		EventReceiverTest eventReceiverTest = applicationContext.getBean(EventReceiverTest.class);

		IEventBusManager eventBusManager = applicationContext.getBean(IEventBusManager.class);
		PlayerLoginTimeEvent event = PlayerLoginTimeEvent.valueOf((int) (System.currentTimeMillis() / 1000L));
		eventBusManager.syncSubmitWithContext(event, 2, "hello2");
	}

	@Test
	public void playerLoginTimeEventSubmitWithContextExTest() {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("eventReceiverTest.xml");

//		EventReceiverTest eventReceiverTest = applicationContext.getBean(EventReceiverTest.class);

		IEventBusManager eventBusManager = applicationContext.getBean(IEventBusManager.class);
		PlayerLoginTimeEvent event = PlayerLoginTimeEvent.valueOf((int) (System.currentTimeMillis() / 1000L));
//		eventBusManager.syncSubmitWithContext(event, 3); todo
		eventBusManager.syncSubmitWithContext(event, 3L);
	}

}
