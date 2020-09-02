package com.linkingluck.midware.utility;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public final class JsonUtils {

	private JsonUtils() {
		throw new IllegalAccessError("该类不允许实例化");
	}

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

	public static String object2String(Object object) {
		return JSON.toJSONString(object, false);
	}

	public static String map2String(Map<?, ?> map) {
		return JSON.toJSONString(map, false);
	}

	private static TypeReference<Map<String, Object>> mapType = new TypeReference<Map<String, Object>>() {
	};


	public static Map<String, Object> string2Map(String content) {
		return JSON.parseObject(content, mapType);
	}

	public static <T> List<T> string2List(String content, Class<T> clz) {
		return JSON.parseArray(content, clz);
	}

	public static <T> T string2Object(String content, Class<T> clz) {
		return JSON.parseObject(content, clz);
	}

	public static <K, V> Map<K, V> string2Map(String content, Class<K> keyType, Class<V> valueType) {
		TypeReference<Map<K, V>> mapType = new TypeReference<Map<K, V>>() {
		};
		return JSON.parseObject(content, mapType);
	}
}
