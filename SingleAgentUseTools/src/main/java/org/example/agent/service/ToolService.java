package org.example.agent.service;

import jakarta.annotation.PostConstruct;
import org.example.agent.anntation.toolprovider;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author caiyuping
 * @date 2026/3/10 10:34
 * @description: 业务
 */
@Service
public class ToolService {
    private final ApplicationContext applicationContext;
    private List<Object> toolInstances;

    public ToolService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init(){
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(toolprovider.class);
        toolInstances = beans.values().stream().collect(Collectors.toList());
    }

    public List<Object> getToolInstances(){
        return toolInstances;
    }

    public Object[] getAllToolProvider(){
        return getToolInstances().toArray();
    }
}
