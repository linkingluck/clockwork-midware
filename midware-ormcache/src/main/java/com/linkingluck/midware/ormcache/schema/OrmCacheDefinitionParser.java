package com.linkingluck.midware.ormcache.schema;

import com.linkingluck.midware.ormcache.IEntity;
import com.linkingluck.midware.ormcache.anno.Cached;
import com.linkingluck.midware.ormcache.anno.Persister;
import com.linkingluck.midware.ormcache.exception.ConfigurationException;
import com.linkingluck.midware.ormcache.persist.PersisterConfig;
import com.linkingluck.midware.ormcache.persist.PersisterType;
import com.linkingluck.midware.utility.Utilitys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OrmCacheDefinitionParser extends AbstractBeanDefinitionParser {

    private static final Logger logger = LoggerFactory.getLogger(OrmCacheDefinitionParser.class);

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        // 注册注入处理器
        registerInjectProcessor(parserContext);

        // 创建工厂类定义
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ServiceManagerFactory.class);

        // 设置存储器
        builder.addPropertyReference(ElementNames.ACCESSOR, getBeanName(element, ElementNames.ACCESSOR));
        // 设置查询器
        builder.addPropertyReference(ElementNames.QUERIER, getBeanName(element, ElementNames.QUERIER));
        // 设置持久化处理器配置
        Map<String, PersisterConfig> persisterConfigs = new HashMap<String, PersisterConfig>();
        Element persisterElement = DomUtils.getChildElementByTagName(element, ElementNames.PERSIST);
        PersisterType type = PersisterType.valueOf(persisterElement.getAttribute(AttributeNames.TYPE));
        String value = persisterElement.getAttribute(AttributeNames.CONFIG);
        persisterConfigs.put(Persister.DEFAULT, new PersisterConfig(type, value));
        for (Element e : DomUtils.getChildElementsByTagName(persisterElement, ElementNames.PERSISTER)) {
            String name = e.getAttribute(AttributeNames.NAME);
            type = PersisterType.valueOf(e.getAttribute(AttributeNames.TYPE));
            value = e.getAttribute(AttributeNames.CONFIG);
            persisterConfigs.put(name, new PersisterConfig(type, value));
        }
        builder.addPropertyValue(ServiceManagerFactory.PERSISTER_CONFIG_NAME, persisterConfigs);

        // 设置实体集合
        Set<Class<? extends IEntity>> classes = new HashSet<>();
        NodeList child = DomUtils.getChildElementByTagName(element, ElementNames.ENTITY).getChildNodes();
        for (int i = 0; i < child.getLength(); i++) {
            Node node = child.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            String name = node.getLocalName();
            if (name.equals(ElementNames.PACKAGE)) {
                // 自动包扫描处理
                String packageName = ((Element) node).getAttribute(AttributeNames.NAME);
                Set<String> classNames = Utilitys.resolve(packageName, Cached.class.getName());
                for (String className : classNames) {
                    Class<? extends IEntity> clz = null;
                    try {
                        clz = (Class<? extends IEntity>) Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        FormattingTuple message = MessageFormatter.format("无法获取的资源类[{}]", className);
                        logger.error(message.getMessage());
                        throw new ConfigurationException(message.getMessage(), e);
                    }
                    classes.add(clz);
                }
            }

            if (name.equals(ElementNames.CLASS)) {
                // 自动类加载处理
                String className = ((Element) node).getAttribute(AttributeNames.NAME);
                Class<? extends IEntity> clz = null;
                try {
                    clz = (Class<? extends IEntity>) Class.forName(className);
                } catch (ClassNotFoundException e) {
                    FormattingTuple message = MessageFormatter.format("无法获取的资源类[{}]", className);
                    logger.error(message.getMessage());
                    throw new ConfigurationException(message.getMessage(), e);
                }
                classes.add(clz);
            }
        }
        builder.addPropertyValue(ServiceManagerFactory.ENTITY_CLASSES_NAME, classes);

        // 设置常量配置信息
        Element contantsElement =  DomUtils.getChildElementByTagName(element, ElementNames.CONSTANTS);
        String ref = contantsElement.getAttribute(AttributeNames.REF);
        // 引用设置
        if (!StringUtils.isEmpty(ref)) {
            builder.addPropertyReference(ElementNames.CONSTANTS, ref);
        }
        // 指定设置
        ManagedMap<String, Integer> constants = new ManagedMap<String, Integer>();
        for (Element e : DomUtils.getChildElementsByTagName(contantsElement, ElementNames.CONSTANT)) {
            String name = e.getAttribute(AttributeNames.NAME);
            Integer size = Integer.parseInt(e.getAttribute(AttributeNames.SIZE));
            constants.put(name, size);
        }
        builder.addPropertyValue(ServiceManagerFactory.CONSTANTS, constants);

        return builder.getBeanDefinition();
    }

    private String getBeanName(Element element, String tagName) {
        element = ParserHelper.getUniqueChildElementByTagName(element, tagName);
        // 引用处理
        if (element.hasAttribute(AttributeNames.REF)) {
            return element.getAttribute(AttributeNames.REF);
        }
        throw new ConfigurationException("存储器配置声明缺失");
    }

    private void registerInjectProcessor(ParserContext parserContext) {
        BeanDefinitionRegistry registry = parserContext.getRegistry();
        String name = StringUtils.uncapitalize(InjectProcessor.class.getSimpleName());
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(InjectProcessor.class);
        registry.registerBeanDefinition(name, factory.getBeanDefinition());
    }
}
