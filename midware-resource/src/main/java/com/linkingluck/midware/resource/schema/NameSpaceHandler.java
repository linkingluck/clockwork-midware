package com.linkingluck.midware.resource.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 *
 */
public class NameSpaceHandler extends NamespaceHandlerSupport {
	@Override
	public void init() {
		registerBeanDefinitionParser(Schema.CONFIG_ELEMENT, new ConfigDefinitionParser());
	}
}
