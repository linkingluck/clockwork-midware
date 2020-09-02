package com.linkingluck.midware.resource.anno;

import java.lang.annotation.*;

/**
 * 配置资源重新加载：热更会重新加载
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceRoload {

	/**
	 * 需监听的多个class：class对应配置资源
	 *
	 * @return
	 */
	Class[] value();
}
