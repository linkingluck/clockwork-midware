package com.linkingluck.midware.resource.schema;


import com.linkingluck.midware.resource.anno.Resource;
import com.linkingluck.midware.resource.bean.StorageManagerFactory;
import com.linkingluck.midware.resource.model.FormatDefinition;
import com.linkingluck.midware.resource.model.ResourceDefinition;
import com.linkingluck.midware.resource.processor.ResourceReloadProcessor;
import com.linkingluck.midware.resource.processor.StaticInjectProcessor;
import com.linkingluck.midware.resource.reader.ReaderHolder;
import com.linkingluck.midware.resource.reader.impl.ExcelReader;
import com.linkingluck.midware.resource.reader.impl.JsonReader;
import com.linkingluck.midware.resource.reader.impl.PropertiesReader;
import com.linkingluck.midware.utility.Utilitys;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigDefinitionParser extends AbstractBeanDefinitionParser {

	private static final Logger logger = LoggerFactory.getLogger(ConfigDefinitionParser.class);

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		//
		register(parserContext);

		//自定义xml解析成对于的schema
		Schema schema = Schema.resolveSchema(element);
		schema.check();

		//准备好definitions
		List<BeanDefinition> resourceDefinitions = new ManagedList<>();
		Set<String> classNames = new HashSet<>();
		schema.getPackageNodes().forEach(packageNode -> {
			String packagePath = packageNode.getAttribute(Schema.PACKAGE_ELEMENT_ATTRIBUTE_PATH);
			Set<String> result = Utilitys.resolve(packagePath, Resource.class.getName());
			classNames.addAll(result);
		});
		schema.getClassNodes().forEach(classNode -> {
			String className = classNode.getAttribute(Schema.CLASS_ELEMENT_ATTRIBUTE_PATH);
			classNames.add(className);
		});

		FormatDefinition format = parseFormat(schema.getFormatNode());
		for (String className : classNames) {
			Class<?> clz = null;
			try {
				clz = Class.forName(className);
			} catch (ClassNotFoundException e) {
				String message = MessageFormat.format("无法获取的资源类[{0}]", className);
				throw new RuntimeException(message, e);
			}

			BeanDefinition definition = parseResource(clz, format);
			resourceDefinitions.add(definition);
		}

		//构建beanDefinition
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(StorageManagerFactory.class);
		beanDefinitionBuilder.addPropertyValue("definitions", resourceDefinitions);
		return beanDefinitionBuilder.getBeanDefinition();
	}

	private void register(ParserContext parserContext) {
		registerDefaultReader(parserContext);
		registerReaderHolder(parserContext);
		registerStaticInjectProcessor(parserContext);
		registerResourceReloadProcessor(parserContext);
	}

	private void registerDefaultReader(ParserContext parserContext) {
		BeanDefinitionRegistry registry = parserContext.getRegistry();

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ExcelReader.class);
		String name = StringUtils.uncapitalize(ExcelReader.class.getSimpleName());
		registry.registerBeanDefinition(name, builder.getBeanDefinition());

		builder = BeanDefinitionBuilder.rootBeanDefinition(PropertiesReader.class);
		name = StringUtils.uncapitalize(PropertiesReader.class.getSimpleName());
		registry.registerBeanDefinition(name, builder.getBeanDefinition());

		builder = BeanDefinitionBuilder.rootBeanDefinition(JsonReader.class);
		name = StringUtils.uncapitalize(JsonReader.class.getSimpleName());
		registry.registerBeanDefinition(name, builder.getBeanDefinition());
	}

	private void registerReaderHolder(ParserContext parserContext) {
		BeanDefinitionRegistry registry = parserContext.getRegistry();
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ReaderHolder.class);
		String name = StringUtils.uncapitalize(ReaderHolder.class.getSimpleName());
		registry.registerBeanDefinition(name, builder.getBeanDefinition());
	}

	private void registerStaticInjectProcessor(ParserContext parserContext) {
		BeanDefinitionRegistry registry = parserContext.getRegistry();
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(StaticInjectProcessor.class);
		String name = StringUtils.uncapitalize(StaticInjectProcessor.class.getSimpleName());
		registry.registerBeanDefinition(name, builder.getBeanDefinition());
	}

	private void registerResourceReloadProcessor(ParserContext parserContext) {
		BeanDefinitionRegistry registry = parserContext.getRegistry();
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ResourceReloadProcessor.class);
		String name = StringUtils.uncapitalize(ResourceReloadProcessor.class.getSimpleName());
		registry.registerBeanDefinition(name, builder.getBeanDefinition());
	}

	private BeanDefinition parseResource(Class<?> clz, FormatDefinition format) {
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(ResourceDefinition.class);
		beanDefinitionBuilder.addConstructorArgValue(clz);
		beanDefinitionBuilder.addConstructorArgValue(format);
		return beanDefinitionBuilder.getBeanDefinition();
	}

	private FormatDefinition parseFormat(Element e) {
		String type = e.getAttribute(Schema.FORMAT_ELEMENT_ATTRIBUTE_TYPE);
		String location = e.getAttribute(Schema.FORMAT_ELEMENT_ATTRIBUTE_LOCATION);
		String suffix = e.getAttribute(Schema.FORMAT_ELEMENT_ATTRIBUTE_SUFFIX);
		if (org.apache.commons.lang3.StringUtils.endsWith(location, File.pathSeparator)) {
			location = org.apache.commons.lang3.StringUtils.substringAfterLast(location, File.pathSeparator);
		}
		return FormatDefinition.valueOf(type, location, suffix);
	}

}
