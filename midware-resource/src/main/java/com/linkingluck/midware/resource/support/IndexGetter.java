package com.linkingluck.midware.resource.support;

import java.util.Comparator;

public interface IndexGetter extends Getter {

	/**
	 * 索引名
	 *
	 * @return
	 */
	String getName();

	/**
	 * 是否唯一索引
	 *
	 * @return
	 */
	boolean isUnique();

	/**
	 * 索引排序器
	 *
	 * @return
	 */
	Comparator<?> getComparator();

	/**
	 * 是否存在索引排序器
	 *
	 * @return
	 */
	boolean hasComparator();

}
