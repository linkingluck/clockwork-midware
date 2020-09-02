package com.linkingluck.midware.resource.model;

import com.linkingluck.midware.resource.anno.Resource;
import com.linkingluck.midware.resource.anno.ResourceClassFieldInject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ResourceDefinition {

	public final static String DOT_SPLIT = ".";

	/**
	 * 资源类
	 **/
	private Class<?> clz;

	/**
	 * 资源是否必需
	 **/
	private boolean required;

	/**
	 * 资源格式
	 **/
	private String format;

	/**
	 * 资源路径
	 **/
	private String location;

	/**
	 * 资源后缀
	 **/
	private String suffix;

	private boolean cache;

	private boolean openClone;

	/**
	 * 资源类字段注入信息
	 */
	private Set<InjectDefinition> fieldInjects = new HashSet<>();

	public ResourceDefinition(Class<?> clz, FormatDefinition formatDefinition) {
		this.clz = clz;
		Resource anno = clz.getAnnotation(Resource.class);
		this.required = anno.required();
		this.cache = anno.cache();
		this.openClone = anno.openClone();
		if (StringUtils.isBlank(anno.absolutePath())) {
			this.suffix = formatDefinition.getDefaultSuffix();
			this.location = clz.getSimpleName() + DOT_SPLIT + suffix;
		} else {
			this.suffix = location.substring(location.lastIndexOf("."));
			this.location = anno.absolutePath();
		}
		if (!StringUtils.isBlank(anno.suffix())) {
			this.suffix = anno.suffix();
			this.location = clz.getSimpleName() + DOT_SPLIT + suffix;
		}

		ReflectionUtils.doWithFields(clz, field -> fieldInjects.add(new InjectDefinition(field)),
			field -> field.isAnnotationPresent(ResourceClassFieldInject.class));
	}

	/**
	 * 获取被注入静态域
	 *
	 * @return
	 */
	public Set<InjectDefinition> getStaticInjects() {
		Predicate<InjectDefinition> predicate = injectDefinition -> Modifier.isStatic(injectDefinition.getField().getModifiers());
		return fieldInjects.stream().filter(predicate).collect(Collectors.toSet());
	}

	/**
	 * 获取被注入实例域
	 *
	 * @return
	 */
	public Set<InjectDefinition> getInstanceInjects() {
		Predicate<InjectDefinition> predicate = injectDefinition -> Modifier.isStatic(injectDefinition.getField().getModifiers());
		return fieldInjects.stream().filter(predicate.negate()).collect(Collectors.toSet());
	}

	public Class<?> getClz() {
		return clz;
	}

	public boolean isRequired() {
		return required;
	}

	public String getFormat() {
		return format;
	}

	public String getLocation() {
		return location;
	}

	public String getSuffix() {
		return suffix;
	}

	public boolean isCache() {
		return cache;
	}

	public boolean isOpenClone() {
		return openClone;
	}

	public Set<InjectDefinition> getFieldInjects() {
		return fieldInjects;
	}
}
