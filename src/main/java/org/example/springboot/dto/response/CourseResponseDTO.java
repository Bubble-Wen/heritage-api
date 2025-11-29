package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程列表响应DTO
 * @author system
 */
@Data
@Schema(description = "课程列表响应")
public class CourseResponseDTO {

    @Schema(description = "课程ID")
    private String id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "难度等级")
    private String level;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "封面文件ID")
    private Long coverFileId;

    @Schema(description = "封面文件路径")
    private String coverFilePath;

    @Schema(description = "章节数")
    private Long chapterCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}


