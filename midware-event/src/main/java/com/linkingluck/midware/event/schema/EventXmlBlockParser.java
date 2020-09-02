package com.linkingluck.midware.event.schema;

import com.linkingluck.midware.event.anno.EventReceiver;
import com.linkingluck.midware.event.beanpostprocessor.ReceiverBeanPostProcessor;
import com.linkingluck.midware.event.core.AbstractEventBusManager;
import com.linkingluck.midware.event.core.IEventBusManager;
import com.linkingluck.midware.utility.Utilitys;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import java.text.MessageFormat;
import java.util.Set;

public class EventXmlBlockParser implements BeanDefinitionParser {

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		return parseInternal(element, parserContext);
	}

	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		registerBeanPostProcessor(parserContext);

		Schema schema = Schema.resolveSchema(element);
		schema.check();

		registerAllReceivers(parserContext, schema);

		Class clz = null;
		try {
			clz = Class.forName(schema.getEventBus());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("not found class:" + schema.getEventBus());
		}

		//构建beanDefinition
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(clz);
		String ref = schema.getRef();
		if (!StringUtils.isEmpty(ref)) {
			beanDefinitionBuilder.addPropertyReference(AbstractEventBusManager.EVENT_CHOICER, ref);
		}
		AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
		//向容器注册
		parserContext.getRegistry().registerBeanDefinition(StringUtils.uncapitalize(IEventBusManager.class.getSimpleName()), beanDefinition);
		return beanDefinition;
	}

	private void registerBeanPostProcessor(ParserContext parserContext) {
		BeanDefinitionRegistry registry = parserContext.getRegistry();
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ReceiverBeanPostProcessor.class);
		String name = StringUtils.uncapitalize(ReceiverBeanPostProcessor.class.getSimpleName());
		registry.registerBeanDefinition(name, builder.getBeanDefinition());
	}

	/**
	 * 向容器注册所有EventReceiver注解的类
	 *
	 * @param parserContext
	 * @param schema
	 */
	private void registerAllReceivers(ParserContext parserContext, Schema schema) {
		Set<String> classNames = Utilitys.resolve(schema.getBasePackage(), EventReceiver.class.getName());
		classNames.forEach(className -> {
			try {
				Class clz = Class.forName(className);
				BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(clz);
				parserContext.getRegistry().registerBeanDefinition(StringUtils.uncapitalize(clz.getSimpleName()), beanDefinitionBuilder.getBeanDefinition());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				String message = MessageFormat.format("无法获取类[{0}]", className);
				throw new RuntimeException(message, e);
			}
		});

	}


}
