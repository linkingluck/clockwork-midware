package com.linkingluck.midware.event;

import com.linkingluck.midware.event.anno.EventReceiver;
import com.linkingluck.midware.event.core.IEventBusManager;
import com.linkingluck.midware.event.event.PlayerExitTimeEvent;
import com.linkingluck.midware.event.event.PlayerLoginTimeEvent;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TwoEventReceiverTest {

	@EventReceiver
	public void playerLoginTimeEventHandler(PlayerLoginTimeEvent event, int arg1, String arg2) {
		System.out.println(event.getTimestamp());
		System.out.println(arg1);
		System.out.println(arg2);
	}

	@EventReceiver
	public void PlayerExitTimeEventHandler(PlayerExitTimeEvent event, long arg1, List<Integer> arg2) {
		System.out.println(event.getTimestamp());
		System.out.println(arg1);
		System.out.println(Arrays.toString(arg2.toArray()));
	}

	@Test
	public void twoEventSubmitTest() {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("twoEventReceiverTest.xml");

//		TwoEventReceiverTest eventReceiverTest = applicationContext.getBean(TwoEventReceiverTest.class);

		IEventBusManager eventBusManager = applicationContext.getBean(IEventBusManager.class);
		eventBusManager.syncSubmitWithContext(PlayerLoginTimeEvent.valueOf((int) (System.currentTimeMillis() / 1000L)), 1, "hello2");
		eventBusManager.syncSubmitWithContext(PlayerExitTimeEvent.valueOf("wanJ", (int) (System.currentTimeMillis() / 1000L)), 1L, new ArrayList<>());
	}

}
