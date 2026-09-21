package com.youfu.aiagent.app;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;
import jakarta.annotation.Resource;
import com.youfu.aiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;


@Component
@Slf4j
public class LoveApp {


    private final ChatClient chatClient;


    private static final String SYSTEM_PROMPT =
            "扮演深耕恋爱心理领域的专家。" +
                    "开场向用户表明身份，告知用户可倾诉恋爱难题。" +
                    "围绕单身、恋爱、已婚三种状态提问：" +
                    "单身状态询问社交圈拓展及追求心仪对象的困扰；" +
                    "恋爱状态询问沟通、习惯差异引发的矛盾；" +
                    "已婚状态询问家庭责任与亲属关系处理的问题。" +
                    "引导用户详述事情经过、对方反应及自身想法，" +
                    "以便给出专属解决方案。";


    public LoveApp(ChatModel dashscopeChatModel) {


        // 创建聊天记忆
        ChatMemory chatMemory =
                MessageWindowChatMemory.builder()
                        .maxMessages(10)
                        .build();


        // 创建ChatClient
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .build()
                )
                .build();

    }


    public String doChat(String message, String chatId) {


        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec
                        .param("chat_memory_conversation_id", chatId)
                        .param("chat_memory_retrieve_size", 10)
                )
                .call()
                .chatResponse();


        String content =
                response.getResult()
                        .getOutput()
                        .getText();


        log.info("content: {}", content);


        return content;
    }

    @Resource
    private VectorStore loveAppVectorStore;

    public String doChatWithRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec
                        .param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(new MyLoggerAdvisor())
                .advisors(QuestionAnswerAdvisor
                                .builder(loveAppVectorStore)

                        .build())
                .call()
                .chatResponse();

        String content = chatResponse
                .getResult()
                .getOutput()
                .getText();

        log.info("content: {}", content);
        return content;
    }
}


