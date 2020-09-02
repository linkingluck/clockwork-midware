package com.linkingluck.midware.resource.schema;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigDefinitionParser2 extends AbstractSingleBeanDefinitionParser {

	@Override
	protected Class<?> getBeanClass(Element element) {
		return Schema.class;
	}

	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			String nodeName = node.getLocalName();
			if (nodeName == null) {
				continue;
			}
			switch (nodeName) {
				case Schema.FORMAT_ELEMENT:
					String name = ((Element) node).getAttribute(Schema.FORMAT_ELEMENT_ATTRIBUTE_NAME);
					builder.addPropertyValue("formatSuffix", name);
					break;
				case Schema.PACKAGE_ELEMENT:
					String path = ((Element) node).getAttribute(Schema.PACKAGE_ELEMENT_ATTRIBUTE_PATH);
					builder.addPropertyValue("packageDir", path);
					break;
				case Schema.CLASS_ELEMENT:
					String path1 = ((Element) node).getAttribute(Schema.CLASS_ELEMENT_ATTRIBUTE_PATH);
					builder.addPropertyValue("classPath", path1);
					break;
				default:
					break;
			}
		}

	}
}
