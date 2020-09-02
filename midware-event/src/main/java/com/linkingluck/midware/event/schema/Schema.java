package com.linkingluck.midware.event.schema;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Schema {

	/**
	 * config标签
	 */
	public static final String CONFIG_ELEMENT = "config";

	/**
	 * config标签-属性basePackage
	 */
	public static final String CONFIG_ELEMENT_ATTRIBUTE_BASE_PACKAGE = "basePackage";

	/**
	 * config标签-属性eventBus
	 */
	public static final String CONFIG_ELEMENT_ATTRIBUTE_EVENT_BUS = "eventBus";

	/**
	 * eventChoicer标签
	 */
	public static final String EVENT_CHOICE_ELEMENT = "eventChoicer";

	/**
	 * eventChoicer标签-ref属性
	 */
	public static final String EVENT_CHOICE_ELEMENT_ATTRIBUTE_REF = "ref";


	private Element config;
	private String basePackage;
	private String eventBus;

	private Element eventChoicer;
	private String ref;

	public Schema() {

	}

	static Schema resolveSchema(Element element) {
		Schema schema = new Schema();
		if (element.getLocalName().equalsIgnoreCase(CONFIG_ELEMENT)) {
			schema.setConfig(element);
			String basePackage = element.getAttribute(CONFIG_ELEMENT_ATTRIBUTE_BASE_PACKAGE);
			schema.setBasePackage(basePackage);
			String eventBus = element.getAttribute(CONFIG_ELEMENT_ATTRIBUTE_EVENT_BUS);
			schema.setEventBus(eventBus);
		}
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			String nodeName = node.getLocalName();
			if (nodeName == null) {
				continue;
			}
			Element e = (Element) node;
			switch (nodeName) {
				case EVENT_CHOICE_ELEMENT:
					schema.setEventChoicer(e);
					String ref = e.getAttribute(EVENT_CHOICE_ELEMENT_ATTRIBUTE_REF);
					schema.setRef(ref);
					break;
				default:
					break;
			}
		}
		return schema;
	}

	public void check() {

	}


	public Element getConfig() {
		return config;
	}

	public void setConfig(Element config) {
		this.config = config;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public Element getEventChoicer() {
		return eventChoicer;
	}

	public void setEventChoicer(Element eventChoicer) {
		this.eventChoicer = eventChoicer;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getEventBus() {
		return eventBus;
	}

	public void setEventBus(String eventBus) {
		this.eventBus = eventBus;
	}
}
