package com.linkingluck.midware.ormcache.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class NameSpaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser(ElementNames.CONFIG_ELEMENT, new OrmCacheDefinitionParser());
    }
}
