package com.linkingluck.midware.resource.util;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ResourceUtils {

	private static final String LOCAL_DIR = ".local";

	public static Resource findResource(ApplicationContext applicationContext, String location) throws IOException {
		return findResource(LOCAL_DIR, applicationContext, location);
	}

	private static Resource findResource(String localDir, ApplicationContext applicationContext, String location) throws IOException {
		return findResources(LOCAL_DIR, applicationContext, location);
	}

	private static Resource findResources(ApplicationContext applicationContext, String... locations) throws IOException {
		return findResources(LOCAL_DIR, applicationContext, locations);
	}

	private static Resource findResources(String localDir, ApplicationContext applicationContext, String... locations) throws IOException {

		for (String location : locations) {
			Resource resource = fastLoad(localDir, applicationContext, location);
			if (resource != null) {
				return resource;
			}
		}

		String message = MessageFormat.format("没法找到对应的资源[{0}]", locations);
		throw new RuntimeException(message);
	}


	private static Set<String> suffixCache = new CopyOnWriteArraySet<>();

	private synchronized static Resource fastLoad(String localDir, ApplicationContext applicationContext, String filename) throws IOException {
		String suffix = filename.substring((filename.lastIndexOf(".")));

		if (!suffixCache.contains(suffix)) {
			cacheSuffix(applicationContext, suffix);
		}

		Collection<Resource> resources = resourceCache.get(filename);
		if (resources == null) {
			return null;
		}

		for (Resource resource : resources) {
			if (!resource.getURI().getPath().endsWith(filename)) {
				continue;
			}

			if (StringUtils.isBlank(LOCAL_DIR) && resource.getURL().getFile().contains(localDir)) {
				return resource;
			}

			return resource;
		}

		return null;
	}

	private static Multimap<String, Resource> resourceCache = ArrayListMultimap.create();

	private static void cacheSuffix(ApplicationContext applicationContext, String suffix) throws IOException {
		if (suffixCache.contains(suffix)) {
			return;
		}

		suffixCache.add(suffix);
		StringBuilder builder = new StringBuilder("classpath:").append("**").append(File.separator).append("*").append(suffix);
		Resource[] resources = applicationContext.getResources(builder.toString());
		for (Resource resource : resources) {
			resourceCache.put(resource.getFilename(), resource);
		}
	}
}
