package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.springboot.dto.FileInfoDTO;


import java.util.List;

/**
 * 课程章节响应DTO
 * @author system
 */
@Data
@Schema(description = "课程章节响应")
public class CourseChapterResponseDTO {

    @Schema(description = "章节ID")
    private Long id;

    @Schema(description = "课程ID")
    private String courseId;

    @Schema(description = "章节标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "视频文件列表(通过文件系统关联查询)")
    private List<FileInfoDTO> videoFiles;
}

