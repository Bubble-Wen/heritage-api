package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI聊天消息响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI聊天消息响应DTO")
public class AiChatMessageResponseDTO {

    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "角色：user-用户，assistant-AI助手")
    private String role;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

