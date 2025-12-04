package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 活动报名创建命令DTO
 * @author system
 */
@Data
@Schema(description = "活动报名创建命令")
public class ActivitySignupCreateCommandDTO {

    @Schema(description = "活动ID")
    @NotNull(message = "活动ID不能为空")
    private String activityId;

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long userId;
}

