package org.example.langchain4jmodel.graph.WrapperNode;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import org.example.langchain4jmodel.agent.chathistory.HistoryAgent;
import org.example.langchain4jmodel.agent.search.SearchAgent;
import org.example.langchain4jmodel.graph.AgentState;

/**
 * @author caiyuping
 * @date 2026/3/7 12:56
 * @description: 业务
 */
public class SaveMsgNode implements GraphNode{

    private final HistoryAgent historyAgent;

    public SaveMsgNode(HistoryAgent historyAgent) {
        this.historyAgent = historyAgent;
    }
    @Override
    public String getName() {
        return "save_msg";
    }

    @Override
    public String getDescription() {
        return "【后续步骤】专门用于保存【上一步已获取的】重要数据（如天气结果、查询结论等等）。" +
                "警告：如果对话中还没有具体的查询结果（例如还没调用 weather_node等数据生成节点），绝对不要调用此节点！" +
                "必须先确保数据已经存在于最近的 AI 回复中。";
    }

    @Override
    public AgentState process(AgentState state, ChatMemory chatMemory) {
        //这个是获取到用户的提问
        String lastUserText = chatMemory.messages().stream()
                .filter(m -> m instanceof UserMessage)
                .map(m -> ((UserMessage) m).singleText())
                .reduce((first, second) -> second) // 取最后一个
                .orElse("No user input found");
        String lastAiResult = chatMemory.messages().stream()
                .filter(m -> m instanceof dev.langchain4j.data.message.AiMessage)
                .map(m -> ((dev.langchain4j.data.message.AiMessage) m).text())
                .reduce((first, second) -> second) // 取最新的一条
                .orElse("无具体执行结果");
        System.out.println("SaveMsgNode processing: " + lastUserText);
        String contextForSaving = String.format(
                "任务：保存重要信息。\n" +
                        "用户原始请求：%s\n\n" +
                        "系统已执行并获取到的【确切结果】如下（请务必原样保存此部分数据）：\n" +
                        "--- BEGIN DATA ---\n" +
                        "%s\n" +
                        "--- END DATA ---\n" +
                        "请提取上述【确切结果】中的关键事实进行持久化存储。",
                lastUserText,
                lastAiResult
        );
        //这个是调用searchAgent进行回复，同时保存到内存中
        String response = historyAgent.analyzeAndSave(contextForSaving);
        chatMemory.add(new AiMessage(response));
        //修改共享内存的消息
        state.setVariable("last_save_result", response);
        state.setCurrentStep("savemsg_node");
        state.setNextNode("__DECIDE__");
        return state;
    }
}
