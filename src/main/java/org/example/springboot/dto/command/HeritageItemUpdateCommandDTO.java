package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 非遗作品更新命令DTO
 * @author system
 */
@Data
@Schema(description = "非遗作品更新命令")
public class HeritageItemUpdateCommandDTO {

    @Schema(description = "标题")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    @Schema(description = "类别")
    @Size(max = 100, message = "类别长度不能超过100个字符")
    private String category;

    @Schema(description = "地区")
    @Size(max = 100, message = "地区长度不能超过100个字符")
    private String region;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态 0草稿 1待审 2已发布 3下架", example = "0")
    private Integer status;

    @Schema(description = "封面文件ID")
    private Long coverFileId;
}

