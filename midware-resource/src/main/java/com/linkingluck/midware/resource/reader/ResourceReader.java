package com.linkingluck.midware.resource.reader;

import java.io.InputStream;
import java.util.List;

public interface ResourceReader {

	/**
	 * 资源格式
	 *
	 * @return
	 */
	String getSuffix();

	/**
	 * 是否是批量数据
	 *
	 * @return
	 */
	boolean isBatch();

	/**
	 * @param clz         资源类
	 * @param inputStream 文件输入
	 * @param <E>
	 * @return 返回资源类列表
	 */
	<E> List<E> read(Class<E> clz, InputStream inputStream);
}
