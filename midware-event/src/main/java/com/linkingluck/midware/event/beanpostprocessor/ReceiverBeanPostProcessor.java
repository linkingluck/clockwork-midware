package com.linkingluck.midware.event.beanpostprocessor;

import com.linkingluck.midware.event.core.IEventBusManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ReceiverBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		if (!IEventBusManager.class.isAssignableFrom(bean.getClass())) {
			getEventBus().registerReceiver(bean);
		}

		return super.postProcessAfterInstantiation(bean, beanName);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	private IEventBusManager getEventBus() {
		return applicationContext.getBean(IEventBusManager.class);
	}
}
