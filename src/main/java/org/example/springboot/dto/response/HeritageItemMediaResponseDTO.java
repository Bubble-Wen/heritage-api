package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 非遗作品媒体响应DTO
 * @author system
 */
@Data
@Schema(description = "非遗作品媒体响应")
public class HeritageItemMediaResponseDTO {

    @Schema(description = "媒体关联ID")
    private Long id;

    @Schema(description = "文件ID")
    private Long fileId;

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "文件大小")
    private Long fileSize;

    @Schema(description = "媒体类型")
    private String type;

    @Schema(description = "排序")
    private Integer sort;
}

