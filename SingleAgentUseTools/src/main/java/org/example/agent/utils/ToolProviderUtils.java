package org.example.agent.utils;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import dev.langchain4j.service.tool.ToolProviderResult;

import java.lang.reflect.Method;

/**
 * @author caiyuping
 * @date 2026/3/10 10:15
 * @description: 封装工具类的utils
 */
public class ToolProviderUtils {
    public static ToolProviderResult extractToolsFromInstance(Object targetInstance) {
        if(targetInstance == null) throw new IllegalArgumentException("工具为空，请保证输入的tool不为空");
        ToolProviderResult.Builder builder = ToolProviderResult.builder();
        // 获取到所有的tool方法
        Method[] methods = targetInstance.getClass().getMethods();
        for(Method method : methods){
            // 这里要保证只注册有tool的工具
            if (method.isAnnotationPresent(Tool.class)) {
                ToolSpecification toolSpecification = ToolSpecifications.toolSpecificationFrom(method);
                DefaultToolExecutor defaultToolExecutor = new DefaultToolExecutor(toolSpecification, method);
                builder.add(toolSpecification,defaultToolExecutor);
            }
        }
        return builder.build();
    }
}
