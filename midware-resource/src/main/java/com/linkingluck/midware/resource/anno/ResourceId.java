package com.linkingluck.midware.resource.anno;

import java.lang.annotation.*;

/**
 * 配置资源唯一标识列注解
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceId {


}
