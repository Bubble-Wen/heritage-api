package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 传承人关联作品响应DTO
 * @author system
 */
@Data
@Schema(description = "传承人关联作品响应")
public class InheritorItemResponseDTO {

    @Schema(description = "作品ID")
    private String id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "类别")
    private String category;

    @Schema(description = "地区")
    private String region;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;
}

