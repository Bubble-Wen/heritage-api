package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动创建命令DTO
 * @author system
 */
@Data
@Schema(description = "活动创建命令")
public class ActivityCreateCommandDTO {

    @Schema(description = "活动ID（前端UUID预生成）")
    private String id;

    @Schema(description = "标题")
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    @Schema(description = "活动类型")
    @NotBlank(message = "活动类型不能为空")
    @Size(max = 50, message = "活动类型长度不能超过50个字符")
    private String type;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "地点")
    @Size(max = 200, message = "地点长度不能超过200个字符")
    private String location;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态 0草稿 1报名中 2进行中 3已结束")
    private Integer status;

    @Schema(description = "封面文件ID")
    private Long coverFileId;
}


