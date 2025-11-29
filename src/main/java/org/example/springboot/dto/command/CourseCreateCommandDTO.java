package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 课程创建命令DTO
 * @author system
 */
@Data
@Schema(description = "课程创建命令")
public class CourseCreateCommandDTO {

    @Schema(description = "课程ID（前端UUID预生成）")
    private String id;

    @Schema(description = "标题")
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    @Schema(description = "难度等级")
    @Size(max = 50, message = "难度等级长度不能超过50个字符")
    private String level;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态 0草稿 1已发布 2下架")
    private Integer status;

    @Schema(description = "封面文件ID")
    private Long coverFileId;
}


