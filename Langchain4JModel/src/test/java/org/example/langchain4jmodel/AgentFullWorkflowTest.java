package org.example.langchain4jmodel;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.service.memory.ChatMemoryService;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.example.langchain4jmodel.graph.AgentState;
import org.example.langchain4jmodel.graph.OrchestratorAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*; // <--- 1. 引入 Mockito 静态方法

@SpringBootTest
class AgentFullWorkflowTest {

    @Autowired
    private OrchestratorAgent orchestrator;


    @BeforeEach
    void setUp() {
        OrchestratorAgent.clearStaticStore();
    }

    @Test
    void testFullAutoOrchestrationWithRealApp() {
        assertThat(orchestrator).isNotNull();

        System.out.println("[完整项目模式] 开始测试...");

        AgentState finalState = orchestrator.run("test-session-001", "今天北京天气怎么样？");
        //消息打印
        Map<String, Object> variables = finalState.getVariables();
        Set<Map.Entry<String, Object>> entries = variables.entrySet();
        Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
        while(iterator.hasNext()){
            Map.Entry<String, Object> next = iterator.next();
            String key = next.getKey();
            Object value = next.getValue();
            System.out.println(key+" "+value);
        }

        //断言测试
        assertThat(finalState).isNotNull();
        assertThat(finalState.isFinished()).isTrue();

        System.out.println("测试通过！");
    }

    @Test
    void m2() {
        assertThat(orchestrator).isNotNull();

        System.out.println("[完整项目模式] 开始测试...");

        AgentState finalState = orchestrator.run("test-session-001", "用户的Id为134，今天赣州天气怎么样？请进行保存");
        //消息打印
        Map<String, Object> variables = finalState.getVariables();
        Set<Map.Entry<String, Object>> entries = variables.entrySet();
        Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
        while(iterator.hasNext()){
            Map.Entry<String, Object> next = iterator.next();
            String key = next.getKey();
            Object value = next.getValue();
            System.out.println(key+" "+value);
        }

        //断言测试
        assertThat(finalState).isNotNull();
        assertThat(finalState.isFinished()).isTrue();

        System.out.println("最后的总结："+finalState.getVariable("final_summary"));
        System.out.println("测试通过！");
    }
}