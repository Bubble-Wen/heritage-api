package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI聊天请求DTO
 * @author system
 */
@Data
@Schema(description = "AI聊天请求DTO")
public class AiChatCommandDTO {

    @Schema(description = "会话ID", required = true)
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    @Schema(description = "用户消息", required = true)
    @NotBlank(message = "用户消息不能为空")
    private String userMessage;
}

