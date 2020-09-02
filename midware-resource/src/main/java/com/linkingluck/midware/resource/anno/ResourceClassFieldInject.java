package com.linkingluck.midware.resource.anno;

/**
 * 资源类的field注入
 */
public @interface ResourceClassFieldInject {

	/**
	 * 按名字注入,为空按类型注入
	 **/
	String value() default "";

}
