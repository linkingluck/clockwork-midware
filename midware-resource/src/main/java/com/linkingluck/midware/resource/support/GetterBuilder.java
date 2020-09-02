package com.linkingluck.midware.resource.support;

import com.linkingluck.midware.resource.anno.ResourceId;
import com.linkingluck.midware.resource.anno.ResourceIndex;
import com.linkingluck.midware.resource.bean.StorageManagerFactory;
import com.linkingluck.midware.resource.util.ReflectionUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;

public class GetterBuilder {

	private static final Logger logger = LoggerFactory.getLogger(StorageManagerFactory.class);


	private static class IndentityInfo {
		private Field field;
		private Method method;

		public IndentityInfo(Class<?> clz) {
			Field[] fields = ReflectionUtility.getDeclaredFieldsWith(clz, ResourceId.class);
			if (fields.length != 1) {
				String message = MessageFormat.format("类[{0}]没有ResourceId注解或出现在多个字段上", clz.getName());
				logger.error(message);
				throw new RuntimeException(message);
			}
			if (fields.length == 1) {
				this.field = fields[0];
				return;
			}

			Method[] methods = ReflectionUtility.getDeclaredGetMethodsWith(clz, ResourceId.class);
			if (methods.length != 1) {
				String message = MessageFormat.format("类[{0}]没有ResourceId注解或出现在多个方法上", clz.getName());
				logger.error(message);
				throw new RuntimeException(message);
			}
			if (methods.length == 1) {
				this.method = methods[0];
				return;
			}


			String message = MessageFormat.format("类[{0}]没有ResourceId注解", clz.getName());
			logger.error(message);
			throw new RuntimeException(message);
		}


		public boolean isField() {
			return field != null ? true : false;
		}

	}

	private static class FiledGetter implements Getter {

		private Field field;

		public FiledGetter(Field field) {
			this.field = field;
			ReflectionUtility.makeAccessible(field);

		}

		@Override
		public Object getValue(Object o) {
			Object value = null;
			try {
				value = field.get(o);
			} catch (Exception e) {
				String message = "标识属性访问异常";
				logger.error(message);
				throw new RuntimeException(message);
			}

			return value;
		}
	}

	private static class MethodGetter implements Getter {

		private Method method;

		public MethodGetter(Method method) {
			this.method = method;
			ReflectionUtility.makeAccessible(method);

		}

		@Override
		public Object getValue(Object o) {
			Object value = null;
			try {
				value = method.invoke(o);
			} catch (Exception e) {
				String message = "标识方法访问异常";
				logger.error(message);
				throw new RuntimeException(message);
			}

			return value;
		}
	}

	public static Getter createIdGetter(Class<?> clz) {
		IndentityInfo indentityInfo = new IndentityInfo(clz);
		Getter getter = null;
		if (indentityInfo.isField()) {
			getter = new FiledGetter(indentityInfo.field);
		} else {
			getter = new MethodGetter(indentityInfo.method);
		}

		return getter;
	}

	private static class FieldIndexGetter extends FiledGetter implements IndexGetter {

		private final String name;
		private final boolean unique;
		private final Comparator<?> comparator;

		public FieldIndexGetter(Field field) {
			super(field);
			ResourceIndex index = field.getAnnotation(ResourceIndex.class);
			this.name = index.name();
			this.unique = index.unique();

			Class<Comparator> clz = (Class<Comparator>) index.comparatorClz();
			if (!clz.equals(Comparator.class)) {
				try {
					this.comparator = clz.newInstance();
				} catch (Exception e) {
					String message = MessageFormat.format("索引比较器[{0}]无法实例化", clz.getName());
					throw new IllegalArgumentException(message);
				}
			} else {
				comparator = null;
			}
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean isUnique() {
			return unique;
		}

		@Override
		public Comparator<?> getComparator() {
			return comparator;
		}

		@Override
		public boolean hasComparator() {
			if (!unique && comparator != null) {
				return true;
			}
			return false;
		}
	}

	private static class MethodIndexGetter extends MethodGetter implements IndexGetter {

		private final String name;
		private final boolean unique;
		private final Comparator comparator;

		public MethodIndexGetter(Method method) {
			super(method);
			ResourceIndex index = method.getAnnotation(ResourceIndex.class);
			this.name = index.name();
			this.unique = index.unique();

			Class<Comparator> clz = (Class<Comparator>) index.comparatorClz();
			if (!clz.equals(Comparator.class)) {
				try {
					this.comparator = clz.newInstance();
				} catch (Exception e) {
					String message = MessageFormat.format("索引比较器[{0}]无法实例化", clz.getName());
					throw new IllegalArgumentException(message);
				}
			} else {
				comparator = null;
			}
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean isUnique() {
			return unique;
		}

		@Override
		public Comparator<?> getComparator() {
			return comparator;
		}

		@Override
		public boolean hasComparator() {
			if (!unique && comparator != null) {
				return true;
			}
			return false;
		}
	}

	/**
	 * 创建资源索引
	 *
	 * @param clz
	 * @return
	 */
	public static Map<String, IndexGetter> createIndexGetters(Class<?> clz) {
		Field[] fields = ReflectionUtility.getDeclaredFieldsWith(clz, ResourceIndex.class);
		Method[] methods = ReflectionUtility.getDeclaredGetMethodsWith(clz, ResourceIndex.class);
		List<IndexGetter> getters = new ArrayList<IndexGetter>(fields.length + methods.length);
		for (Field field : fields) {
			IndexGetter getter = new FieldIndexGetter(field);
			getters.add(getter);
		}
		for (Method method : methods) {
			IndexGetter getter = new MethodIndexGetter(method);
			getters.add(getter);
		}

		Map<String, IndexGetter> result = new HashMap<String, IndexGetter>(getters.size());
		for (IndexGetter getter : getters) {
			String name = getter.getName();
			if (result.put(name, getter) != null) {
				String message = MessageFormat.format("资源类[{0}]的索引名[{1}]重复", clz.getSimpleName(), name);
				logger.error(message);
				throw new RuntimeException(message);
			}
		}
		return result;
	}


}
