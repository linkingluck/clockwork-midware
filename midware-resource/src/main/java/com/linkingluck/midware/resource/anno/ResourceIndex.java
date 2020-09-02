package com.linkingluck.midware.resource.anno;

import java.lang.annotation.*;
import java.util.Comparator;

/**
 * 配置资源索引标识列注解
 * 按索引组织资源 按索引名区分不同索引-索引列值作为索引-索引值 组织
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceIndex {

	/**
	 * 索引名：要求同一配置资源小索引名唯一
	 *
	 * @return
	 */
	String name();

	/**
	 * 索引值是否唯一：决定索引与索引值是一对一还是一对多的关系
	 *
	 * @return
	 */
	boolean unique() default false;

	/**
	 * 一对多的关系关系下指定排序器
	 *
	 * @return
	 */
	Class<? extends Comparator> comparatorClz();
}
