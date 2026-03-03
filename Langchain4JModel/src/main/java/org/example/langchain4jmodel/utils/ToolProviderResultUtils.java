package org.example.langchain4jmodel.utils;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.service.tool.DefaultToolExecutor;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.service.tool.ToolProviderRequest;
import dev.langchain4j.service.tool.ToolProviderResult;

import java.lang.reflect.Method;

/**
 * @author caiyuping
 * @date 2026/3/4 16:30
 * @description: 静态工具
 */
public class ToolProviderResultUtils {
    public static ToolProviderResult extractToolsFromInstance(Object targetInstance) {
        if (targetInstance == null) {
            throw new IllegalArgumentException("Target instance cannot be null");
        }

        ToolProviderResult.Builder builder = ToolProviderResult.builder();
        Method[] methods = targetInstance.getClass().getMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(Tool.class)) {
                ToolSpecification toolSpec = ToolSpecifications.toolSpecificationFrom(method);
                ToolExecutor toolExecutor = new DefaultToolExecutor(targetInstance, method);
                builder.add(toolSpec, toolExecutor);
            }
        }

        return builder.build();
    }
}
