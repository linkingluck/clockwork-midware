package com.linkingluck.midware.event.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 *
 */
public class EventNameSpaceHandler extends NamespaceHandlerSupport {
	@Override
	public void init() {
		registerBeanDefinitionParser(Schema.CONFIG_ELEMENT, new EventXmlBlockParser());
	}
}
