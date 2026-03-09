package org.example.agent.anntation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author caiyuping
 * @date 2026/3/10 21:30
 * @description: 表示监听，并引入provider
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface toolprovider {
}
