package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程章节实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("course_chapter")
@Schema(description = "课程章节实体类")
public class CourseChapter {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "课程ID")
    @TableField("course_id")
    @Size(max = 50, message = "课程ID长度不能超过50个字符")
    private String courseId;

    @Schema(description = "章节标题")
    @NotBlank(message = "章节标题不能为空")
    @Size(max = 200, message = "章节标题长度不能超过200个字符")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "排序")
    private Integer sort;

    /**
     * 是否有内容
     */
    public boolean hasContent() {
        return content != null && !content.trim().isEmpty();
    }
}

