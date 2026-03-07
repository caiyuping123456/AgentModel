package org.example.langchain4jmodel.graph.WrapperNode;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import org.example.langchain4jmodel.agent.search.SearchAgent;
import org.example.langchain4jmodel.graph.AgentState;
import org.springframework.stereotype.Component;

/**
 * @author caiyuping
 * @date 2026/3/6 16:42
 * @description: 业务
 */
@Component
public class SearchNode implements GraphNode {

    private final SearchAgent searchAgent;

    public SearchNode(SearchAgent searchAgent) {
        this.searchAgent = searchAgent;
    }

    @Override
    public String getName() {
        return "search_node";
    }

    @Override
    public String getDescription() {
        return "你是一个解答的Agent,用户解答用户的提问";
    }

    @Override
    public AgentState process(AgentState state, ChatMemory chatMemory) {
        //这个是获取到用户的提问
        String lastUserText = chatMemory.messages().stream()
                .filter(m -> m instanceof UserMessage)
                .map(m -> ((UserMessage) m).singleText())
                .reduce((first, second) -> second) // 取最后一个
                .orElse("No user input found");

        System.out.println("SearchNode processing: " + lastUserText);
        //这个是调用searchAgent进行回复，同时保存到内存中
        String response = searchAgent.chat(lastUserText);
        chatMemory.add(new AiMessage(response));
        //修改共享内存的消息
        state.setVariable("last_search_result", response);
        state.setCurrentStep("search_node");
        state.setNextNode("__DECIDE__");
        return state;
    }
}
