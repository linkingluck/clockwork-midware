package com.linkingluck.midware.resource.schema;

import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class Schema {

	/**
	 * xml配置
	 */
	public static final String CONFIG_ELEMENT = "config";

	/**
	 * 资源默认格式
	 */
	public static final String FORMAT_ELEMENT = "format";

	/**
	 * 资源默认格式-属性
	 */
	public static final String FORMAT_ELEMENT_ATTRIBUTE_NAME = "suffix";

	/**
	 * 资源默认格式-默认资源类型
	 */
	public static final String FORMAT_ELEMENT_ATTRIBUTE_TYPE = "type";

	/**
	 * 资源默认格式-默认本地资源路径
	 */
	public static final String FORMAT_ELEMENT_ATTRIBUTE_LOCATION = "location";

	/**
	 * 资源默认格式-默认资源文件后缀
	 */
	public static final String FORMAT_ELEMENT_ATTRIBUTE_SUFFIX = "suffix";

	/**
	 * 要扫描的包目录
	 */
	public static final String PACKAGE_ELEMENT = "package";

	/**
	 * 要扫描的包目录
	 */
	public static final String PACKAGE_ELEMENT_ATTRIBUTE_PATH = "path";

	/**
	 * 要扫描的类路径
	 */
	public static final String CLASS_ELEMENT = "class";

	/**
	 * 要扫描的类路径
	 */
	public static final String CLASS_ELEMENT_ATTRIBUTE_PATH = "path";


	private Element formatNode;

	private List<Element> packageNodes;

	private List<Element> classNodes;

	private String config;

	private String formatSuffix;

	private String packageDir;

	private String classPath;

	public Schema() {
		packageNodes = new ArrayList<>();
		classNodes = new ArrayList<>();
	}

	public Element getFormatNode() {
		return formatNode;
	}

	public void setFormatNode(Element formatNode) {
		this.formatNode = formatNode;
	}

	public List<Element> getPackageNodes() {
		return packageNodes;
	}

	public List<Element> getClassNodes() {
		return classNodes;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public String getFormatSuffix() {
		return formatSuffix;
	}

	public void setFormatSuffix(String formatSuffix) {
		this.formatSuffix = formatSuffix;
	}

	public String getPackageDir() {
		return packageDir;
	}

	public void setPackageDir(String packageDir) {
		this.packageDir = packageDir;
	}

	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	static Schema resolveSchema(Element element) {
		Schema schema = new Schema();
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
				case FORMAT_ELEMENT:
					String name = e.getAttribute(FORMAT_ELEMENT_ATTRIBUTE_NAME);
					schema.setFormatSuffix(name);
					schema.setFormatNode(e);
					break;
				case PACKAGE_ELEMENT:
					String path = e.getAttribute(PACKAGE_ELEMENT_ATTRIBUTE_PATH);
					schema.setPackageDir(path);
					schema.packageNodes.add(e);
					break;
				case CLASS_ELEMENT:
					String path1 = ((Element) node).getAttribute(CLASS_ELEMENT_ATTRIBUTE_PATH);
					schema.setClassPath(path1);
					schema.classNodes.add(e);
					break;
				default:
					break;
			}
		}
		return schema;
	}

	public void check() {
		if (StringUtils.isEmpty(formatSuffix)) {
			throw new RuntimeException("resource schema xml format error!!!: format is empty");
		}

		if (StringUtils.isEmpty(packageDir) && StringUtils.isEmpty(classPath)) {
			throw new RuntimeException("resource schema xml format error!!!:packageDir and classPath are all empty");
		}
	}


}
