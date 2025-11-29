package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动报名响应DTO
 * @author system
 */
@Data
@Schema(description = "活动报名响应")
public class ActivitySignupResponseDTO {

    @Schema(description = "报名ID")
    private Long id;

    @Schema(description = "活动ID")
    private String activityId;

    @Schema(description = "活动标题")
    private String activityTitle;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "状态 0待审 1通过 2拒绝 3已签到")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

