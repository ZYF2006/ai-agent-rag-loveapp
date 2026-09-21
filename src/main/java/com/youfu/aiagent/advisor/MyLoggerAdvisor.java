package com.youfu.aiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

@Slf4j
public class MyLoggerAdvisor implements CallAdvisor {

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public ChatClientResponse adviseCall(
            ChatClientRequest chatClientRequest,
            CallAdvisorChain callAdvisorChain) {

        // 调用前：记录请求
        log.info("AI 请求：{}", chatClientRequest);

        // 继续执行后面的 Advisor / ChatModel
        ChatClientResponse chatClientResponse =
                callAdvisorChain.nextCall(chatClientRequest);

        // 调用后：记录响应
        log.info("AI 响应：{}", chatClientResponse);

        return chatClientResponse;
    }
}