package com.linkingluck.midware.network.anno;

import java.lang.annotation.*;

/**
 * 协议
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SocketPacket {

	int packetId();

	String desc() default "";
}