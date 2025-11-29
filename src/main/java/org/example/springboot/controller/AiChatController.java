package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.ai.HeritageAssistantService;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.AiChatCommandDTO;
import org.example.springboot.dto.response.AiChatMessageResponseDTO;
import org.example.springboot.dto.response.AiChatSessionResponseDTO;
import org.example.springboot.entity.AiChatMessage;
import org.example.springboot.entity.AiChatSession;
import org.example.springboot.service.AiChatSessionService;
import org.example.springboot.util.JwtTokenUtils;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI智能助手控制器
 * @author system
 */
@Slf4j
@RestController
@RequestMapping("/ai-chat")
@RequiredArgsConstructor
@Tag(name = "AI智能助手", description = "非遗知识问答AI服务")
public class AiChatController {

    private final HeritageAssistantService aiService;
    private final AiChatSessionService sessionService;

    /**
     * 创建新会话
     */
    @PostMapping("/session/start")
    @Operation(summary = "创建新会话")
    public Result<String> startSession(@RequestParam(required = false) String title) {
        Long userId = JwtTokenUtils.getCurrentUserId();
        String sessionId = sessionService.createSession(userId, title);
        return Result.success(sessionId);
    }

    /**
     * 获取用户所有会话列表
     */
    @GetMapping("/session/list")
    @Operation(summary = "获取会话列表")
    public Result<List<AiChatSessionResponseDTO>> getSessionList() {
        Long userId = JwtTokenUtils.getCurrentUserId();
        List<AiChatSession> sessions = sessionService.getUserSessions(userId);

        List<AiChatSessionResponseDTO> dtoList = sessions.stream()
                .map(session -> AiChatSessionResponseDTO.builder()
                        .sessionId(session.getSessionId())
                        .title(session.getTitle())
                        .createTime(session.getCreateTime())
                        .updateTime(session.getUpdateTime())
                        .build())
                .collect(Collectors.toList());

        return Result.success(dtoList);
    }

    /**
     * 获取会话消息历史
     */
    @GetMapping("/session/{sessionId}/messages")
    @Operation(summary = "获取会话消息历史")
    public Result<List<AiChatMessageResponseDTO>> getSessionMessages(@PathVariable String sessionId) {
        Long userId = JwtTokenUtils.getCurrentUserId();

        // 验证权限
        if (!sessionService.isSessionOwnedByUser(sessionId, userId)) {
            return Result.error( "无权访问此会话");
        }

        List<AiChatMessage> messages = sessionService.getSessionMessages(sessionId);

        List<AiChatMessageResponseDTO> dtoList = messages.stream()
                .map(msg -> AiChatMessageResponseDTO.builder()
                        .id(msg.getId())
                        .role(msg.getRole())
                        .content(msg.getContent())
                        .createTime(msg.getCreateTime())
                        .build())
                .collect(Collectors.toList());

        return Result.success(dtoList);
    }

    /**
     * 流式对话接口（SSE）
     * 返回 Flux<String> 让 Spring WebFlux 自动处理 SSE 流
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "流式对话（SSE）")
    public Flux<String> chatStream(@Valid @RequestBody AiChatCommandDTO dto) {
        Long userId = JwtTokenUtils.getCurrentUserId();

        // 验证权限
        if (!sessionService.isSessionOwnedByUser(dto.getSessionId(), userId)) {
            return Flux.just("data: 无权访问此会话\n\n");
        }

        log.info("开始流式对话，sessionId: {}, userId: {}", dto.getSessionId(), userId);

        // 返回 Flux，Spring WebFlux 会自动处理 SSE 流
        // Spring 会自动为每个元素添加 "data: " 前缀和 "\n\n" 后缀
        // 所以我们只需要返回内容本身即可
        return aiService.chatStream(dto.getSessionId(), dto.getUserMessage())
                .concatWith(Flux.just("[DONE]"))
                .doOnError(error -> {
                    log.error("AI对话流失败", error);
                })
                .onErrorResume(error ->
                    Flux.just("[ERROR]" + error.getMessage())
                );
    }

    /**
     * 非流式对话接口
     */
    @PostMapping("/chat")
    @Operation(summary = "非流式对话")
    public Result<String> chat(@Valid @RequestBody AiChatCommandDTO dto) {
        Long userId = JwtTokenUtils.getCurrentUserId();

        // 验证权限
        if (!sessionService.isSessionOwnedByUser(dto.getSessionId(), userId)) {
            return Result.error("无权访问此会话");
        }

        String response = aiService.chat(dto.getSessionId(), dto.getUserMessage());
        return Result.success(response);
    }

    /**
     * 更新会话标题
     */
    @PutMapping("/session/{sessionId}/title")
    @Operation(summary = "更新会话标题")
    public Result<Void> updateSessionTitle(
            @PathVariable String sessionId,
            @RequestParam String title
    ) {
        Long userId = JwtTokenUtils.getCurrentUserId();

        // 验证权限
        if (!sessionService.isSessionOwnedByUser(sessionId, userId)) {
            return Result.error("无权访问此会话");
        }

        sessionService.updateSessionTitle(sessionId, title);
        return Result.success();
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/session/{sessionId}")
    @Operation(summary = "删除会话")
    public Result<Void> deleteSession(@PathVariable String sessionId) {
        Long userId = JwtTokenUtils.getCurrentUserId();

        // 验证权限
        if (!sessionService.isSessionOwnedByUser(sessionId, userId)) {
            return Result.error("无权访问此会话");
        }

        sessionService.deleteSession(sessionId);
        return Result.success();
    }
}

