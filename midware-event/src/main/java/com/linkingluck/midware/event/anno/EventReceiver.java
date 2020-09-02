package com.linkingluck.midware.event.anno;

import java.lang.annotation.*;

/**
 * 事件监听者注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.METHOD})
public @interface EventReceiver {

	/**
	 * 监听的事件类型
	 *
	 * @return
	 */
	Class<?> eventClz() default Object.class;
}
