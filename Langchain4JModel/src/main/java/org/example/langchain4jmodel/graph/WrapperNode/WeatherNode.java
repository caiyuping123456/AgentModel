package org.example.langchain4jmodel.graph.WrapperNode;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import org.example.langchain4jmodel.agent.weather.WeatherAgent;
import org.example.langchain4jmodel.graph.AgentState;
import org.example.langchain4jmodel.graph.GraphNode;
import org.springframework.stereotype.Component;

/**
 * @author caiyuping
 * @date 2026/3/6 16:14
 * @description: 业务
 */
@Component
public class WeatherNode implements GraphNode {

    private final WeatherAgent weatherAgent;

    public WeatherNode(WeatherAgent weatherAgent) {
        this.weatherAgent = weatherAgent;
    }

    @Override
    public String getName() {
        return "weather_node";
    }

    @Override
    public AgentState process(AgentState state, ChatMemory chatMemory) {
        // 1. 从 ChatMemory 获取最新的用户消息
        // 注意：ChatMemory.messages() 返回的是经过淘汰策略处理后的完整列表
        String lastUserText = chatMemory.messages().stream()
                .filter(m -> m instanceof UserMessage)
                .map(m -> ((UserMessage) m).singleText())
                .reduce((first, second) -> second) // 取最后一个
                .orElse("No user input found");

        System.out.println("WeatherNode processing: " + lastUserText);

        // 2. 调用子 Agent (子 Agent 此时是无状态的，只处理当前请求)
        // 如果子 Agent 需要更多上下文，你可以把 chatMemory.messages() 传给它，或者让它自己查
        String response = weatherAgent.handleWeatherRequest(lastUserText);

        System.out.println("[WeatherNode] AI 回复: " + response);
        // 3. 【关键】将 AI 的回复写入 ChatMemory
        // 这会自动触发：
        // a. 添加到内存列表
        // b. 调用 ChatMemoryStore.updateMessages() 进行持久化
        // c. 检查 Token 限制，如果超限则自动淘汰旧消息
        chatMemory.add(new AiMessage(response));

        // 4. 更新 State 中的变量
        state.setVariable("last_weather_result", response);
        state.setCurrentStep("weather_node");
        state.setNextNode("__END__");
        return state;
    }
}