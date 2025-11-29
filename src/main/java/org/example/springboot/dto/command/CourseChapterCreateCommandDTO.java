package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 课程章节创建命令DTO
 * @author system
 */
@Data
@Schema(description = "课程章节创建命令")
public class CourseChapterCreateCommandDTO {

    @Schema(description = "课程ID")
    @NotBlank(message = "课程ID不能为空")
    @Size(max = 50, message = "课程ID长度不能超过50个字符")
    private String courseId;

    @Schema(description = "章节标题")
    @NotBlank(message = "章节标题不能为空")
    @Size(max = 200, message = "章节标题长度不能超过200个字符")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "排序")
    @NotNull(message = "排序不能为空")
    private Integer sort;
}

