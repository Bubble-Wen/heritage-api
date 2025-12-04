//package org.example.springboot.ai;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.example.springboot.service.AiChatSessionService;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.memory.ChatMemory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Flux;
//
///**
// * 非遗智能助手AI服务
// * @author system
// */
//@Slf4j
//@Service
//@RequiredArgsConstructor // 构造器注入，无需手动加@Autowired
//public class HeritageAssistantService {
//
//    // 1. 所有依赖通过构造器注入（final + RequiredArgsConstructor），移除@Autowired
//    @Qualifier("open-ai")
//    private final ChatClient chatClient;
//    private final ChatMemory chatMemory;
//    private final AiChatSessionService sessionService;
//
//    /**
//     * 流式对话（带会话记忆）
//     *
//     * @param sessionId 会话ID
//     * @param userMessage 用户消息
//     * @return Flux流式响应
//     */
//    public Flux<String> chatStream(String sessionId, String userMessage) {
//        log.info("开始AI流式对话，sessionId: {}, userMessage: {}", sessionId, userMessage);
//
//        // 保存用户消息
//        sessionService.saveMessage(sessionId, "user", userMessage);
//
//        // 构建流式响应（修正缩进 + 统一用字符串常量"conversationId"）
//        Flux<String> responseFlux = chatClient.prompt()
//                .system(PromptManage.HERITAGE_ASSISTANT_PROMPT) // 确保PromptManage类已定义HERITAGE_ASSISTANT_PROMPT常量
//                .user(userMessage)
//                .advisors(advisorSpec -> advisorSpec
//                        .param("conversationId", sessionId) // 统一使用字符串常量，兼容所有Spring AI版本
//                )
//                .stream()
//                .content();
//
//        // 收集完整响应并保存
//        StringBuilder fullResponse = new StringBuilder();
//        return responseFlux
//                .doOnNext(fullResponse::append)
//                .doOnComplete(() -> {
//                    String assistantMessage = fullResponse.toString();
//                    sessionService.saveMessage(sessionId, "assistant", assistantMessage);
//                    log.info("AI流式对话完成，sessionId: {}, 响应长度: {}", sessionId, assistantMessage.length());
//                })
//                .doOnError(error -> {
//                    log.error("AI流式对话失败，sessionId: {}", sessionId, error);
//                });
//    }
//
//    /**
//     * 非流式对话（一次性返回）
//     *
//     * @param sessionId 会话ID
//     * @param userMessage 用户消息
//     * @return AI响应内容
//     */
//    public String chat(String sessionId, String userMessage) {
//        log.info("开始AI非流式对话，sessionId: {}, userMessage: {}", sessionId, userMessage);
//
//        // 保存用户消息
//        sessionService.saveMessage(sessionId, "user", userMessage);
//
//        try {
//            // 调用AI（核心修复：将ChatMemory.CONVERSATION_ID改为"conversationId"）
//            String assistantMessage = chatClient.prompt()
//                    .system(PromptManage.HERITAGE_ASSISTANT_PROMPT)
//                    .user(userMessage)
//                    .advisors(advisorSpec -> advisorSpec
//                            .param("conversationId", sessionId) // 修复错误引用，与流式方法保持一致
//                    )
//                    .call()
//                    .content();
//
//            // 保存AI响应
//            sessionService.saveMessage(sessionId, "assistant", assistantMessage);
//            log.info("AI非流式对话完成，sessionId: {}, 响应长度: {}", sessionId, assistantMessage.length());
//
//            return assistantMessage;
//
//        } catch (Exception e) {
//            log.error("AI非流式对话失败，sessionId: {}", sessionId, e);
//            throw new RuntimeException("AI服务调用失败：" + e.getMessage(), e);
//        }
//    }
//}