package com.linkingluck.midware.utility;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class Utilitys {

	private static String DEFAULT_RESOURCE_PATTERN = "**/*.class";

	private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

	private static MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

	public static Set<String> resolve(String packageName, String annotationName) {
		//解析搜索地址
		String searchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
				+ resolveBasePackage(packageName) + "/" + DEFAULT_RESOURCE_PATTERN;
		try {
			//找寻annotationName注解的类
			Set<String> result = new HashSet<>();
			Resource[] resources = resourcePatternResolver.getResources(searchPath);
			for (Resource resource : resources) {
				if (!resource.isReadable()) {
					continue;
				}

				MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
				AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
				if (!annotationMetadata.hasAnnotation(annotationName)) {
					continue;
				}

				ClassMetadata classMetadata = metadataReader.getClassMetadata();
				result.add(classMetadata.getClassName());
			}

			return result;
		} catch (IOException e) {
			throw new RuntimeException("无法读取资源", e);
		}
	}

	private static String resolveBasePackage(String basePackage) {
		return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
	}

	public static Resource[] resolveResources(String packageName, String annotationName) {
		//解析搜索地址
		String searchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
				+ resolveBasePackage(packageName) + "/" + DEFAULT_RESOURCE_PATTERN;
		try {
			//找寻annotationName注解的类
			//找寻annotationName注解的类
			Set<Resource> result = new HashSet<>();
			Resource[] resources = resourcePatternResolver.getResources(searchPath);
			for (Resource resource : resources) {
				if (!resource.isReadable()) {
					continue;
				}

				MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
				AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
				if (!annotationMetadata.hasAnnotation(annotationName)) {
					continue;
				}

				result.add(resource);
			}

			return (Resource[]) result.toArray();
		} catch (IOException e) {
			throw new RuntimeException("无法读取资源", e);
		}
	}

	private String[] getResources(String packageName, Predicate<MetadataReader> predicate) {
		try {
			// 搜索资源
			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
					+ resolveBasePackage(packageName) + "/" + DEFAULT_RESOURCE_PATTERN;
			Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
			// 提取资源
			Set<String> result = new HashSet<String>();
			for (Resource resource : resources) {
				if (!resource.isReadable()) {
					continue;
				}

				MetadataReader metaReader = this.metadataReaderFactory.getMetadataReader(resource);
				if(!predicate.test(metaReader)) {
					continue;
				}

				ClassMetadata clzMeta = metaReader.getClassMetadata();
				result.add(clzMeta.getClassName());
			}

			return result.toArray(new String[0]);
		} catch (IOException e) {
			String message = "无法读取资源信息";
			throw new RuntimeException(message, e);
		}
	}

}
