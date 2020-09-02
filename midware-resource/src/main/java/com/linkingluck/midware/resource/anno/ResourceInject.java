package com.linkingluck.midware.resource.anno;

import java.lang.annotation.*;

/**
 * 组织好的配置资源类注入
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceInject {

}
