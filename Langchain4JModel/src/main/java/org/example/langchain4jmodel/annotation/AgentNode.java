package org.example.langchain4jmodel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author caiyuping
 * @date 2026/3/9 10:39
 * @description: 这个使用注解的方式进行Node节点的实例化
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AgentNode {

}
