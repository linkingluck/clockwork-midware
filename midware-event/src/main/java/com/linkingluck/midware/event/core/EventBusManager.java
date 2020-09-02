package com.linkingluck.midware.event.core;

import com.linkingluck.midware.event.anno.EventReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class EventBusManager extends AbstractEventBusManagerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(EventBusManager.class);

	private Map<Class<?>, List<IReceiverInvoke>> eventClz2InvokeMap = new HashMap<>();

	private static EventBusManager instance;

	@PostConstruct
	private void init() {
		instance = this;
	}

	@Override
	public void registerReceiver(Object bean) {
		Class<?> clz = bean.getClass();
		ReflectionUtils.doWithMethods(clz, method -> {
			EventReceiver anno = method.getAnnotation(EventReceiver.class);
			if (anno == null) {
				return;
			}

			Class event = null;
			Class eventClz = anno.eventClz();
			if (eventClz != Object.class) {
				event = eventClz;
			} else if (getEventChoicer() != null) {
				event = getEventChoicer().ChoicerEvent(method);
			} else {
				Parameter[] parameters = method.getParameters();
				event = parameters != null ? parameters[0].getType() : null;
			}

			if (event == null) {
				throw new RuntimeException("event object null,not listener any event object, method name:" + method.getName());
			}

			registerReceiver0(bean, method, event);
		});
	}

	private void registerReceiver0(Object bean, Method method, Class eventClz) {
		ReceiverDefintion receiverDefintion = ReceiverDefintion.valueOf(bean, method, eventClz);

		registerDefinition(eventClz, receiverDefintion);
	}

	private void registerDefinition(Class<?> eventClz, IReceiverInvoke receiverDefintion) {
		eventClz2InvokeMap.putIfAbsent(eventClz, new CopyOnWriteArrayList<>());
//		if(eventClz2InvokeMap.get(eventClz).contains(receiverDefintion)) {
//			logger.error(MessageFormat.format("event receiver [{0}] has registered!!!", receiverDefintion.getBean().getClass().getSimpleName()));
//		}
		eventClz2InvokeMap.get(eventClz).add(receiverDefintion);
	}

	private void doSubmitEvent(Object event, IEventCallBack callBack, boolean throwException, Object... contexts) {
		try {
			EventContext eventContext = EventContext.valueOf(event, contexts);
			List<IReceiverInvoke> receivers = getReceiversByEventClz(event.getClass(), eventContext);
			if (CollectionUtils.isEmpty(receivers)) {
				if (logger.isDebugEnabled()) {
					logger.debug("event {} not have valid receivers: no listener or you need check contexts", event.getClass().getSimpleName());
				}
				return;
			}

			for (IReceiverInvoke receiverInvoke : receivers) {

				try {
					Object returnValue = receiverInvoke.invoke(eventContext);
					if (callBack != null) {
						callBack.callback(returnValue);
					}
				} catch (Exception e) {
					if (callBack != null) {
						callBack.exception(e);
					} else {
						String message = MessageFormat.format("类[{1}]方法[{2}]处理事件[{0}]异常",
								event.getClass().getSimpleName(), receiverInvoke.getBean().getClass().getSimpleName(), receiverInvoke.getMethod().getName());
						e.printStackTrace();
						if (throwException) {
							throw new RuntimeException(message, e);
						} else {
							logger.error(message);
						}
					}
				}

			}
		} catch (Exception e) {
			logger.error("事件处理异常", e);
		}
	}

	private List<IReceiverInvoke> getReceiversByEventClz(Class<?> eventClz, EventContext eventContext) {
		if (!eventClz2InvokeMap.containsKey(eventClz)) {
			return Collections.emptyList();
		}

		return eventClz2InvokeMap.get(eventClz).stream().filter(each -> {
			if (each.getMethod().getParameterCount() > eventContext.getParameterCount()) {
				return false;
			}
			for (Class<?> parameterType : each.getMethod().getParameterTypes()) {
				if (!eventContext.hasParameterType(parameterType)) {
					return false;
				}
			}

			return true;
		}).collect(Collectors.toList());
	}

	@Override
	public void syncSubmit(Object event) {
		doSubmitEvent(event, null, false);
	}

	@Override
	public void syncSubmit(Object event, IEventCallBack callBack) {
		doSubmitEvent(event, callBack, false);
	}

	@Override
	public void syncSubmitWithContext(Object event, Object... contexts) {
		doSubmitEvent(event, null, false, contexts);
	}

	@Override
	public void syncSubmitWithContext(Object event, IEventCallBack callBack, Object... contexts) {
		doSubmitEvent(event, callBack, false, contexts);
	}

	public static EventBusManager getInstance() {
		return instance;
	}
}
