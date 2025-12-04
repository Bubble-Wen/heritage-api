package org.example.springboot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ChatClient Configuration Class
 *
 * @author system
 */
@Configuration
public class ChatClientConfig {

    public static final Integer MAX_MEMORY_MESSAGE_SIZE = 30;

    /**
     * Configures ChatMemory - in-memory session storage (MessageWindowChatMemory 是具体实现类)
     *
     * @return ChatMemory instance
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(MAX_MEMORY_MESSAGE_SIZE) // 最多保留30条对话记忆
                .build();
    }

    /**
     * Configures ChatClient - OpenAI compatible chat client
     *
     * @param openAiChatModel OpenAI Chat Model（Spring 自动配置，依赖 application.properties 中的参数）
     * @param chatMemory      注入上面定义的 ChatMemory Bean（避免重复创建）
     * @return ChatClient instance（Bean 名称 "open-ai"，与业务类中 @Qualifier 对应）
     */
    @Bean("open-ai")
    public ChatClient openAIChatClient(OpenAiChatModel openAiChatModel, ChatMemory chatMemory) {
        return ChatClient.builder(openAiChatModel)
                // 关键修正：传入注入的 chatMemory，而非重新调用 chatMemory()
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }
}