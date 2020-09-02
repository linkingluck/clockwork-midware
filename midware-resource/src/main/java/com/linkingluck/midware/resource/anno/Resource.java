package com.linkingluck.midware.resource.anno;

import java.lang.annotation.*;

/**
 * 配置资源注解
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Resource {


	/**
	 * 指定资源名称（默认按类名）
	 *
	 * @return
	 */
	String value() default "";

	/**
	 * 资源绝对路径（默认在resources目录按名字检索）
	 *
	 * @return
	 */
	String absolutePath() default "";

	/**
	 * 配置文件是否必需（默认必需）
	 *
	 * @return
	 */
	boolean required() default true;

	/**
	 * 数据源是否缓存
	 **/
	boolean cache() default false;

	boolean openClone() default false;

	String suffix() default "";

}
