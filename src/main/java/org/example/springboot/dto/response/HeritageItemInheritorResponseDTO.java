package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 非遗作品传承人响应DTO
 * @author system
 */
@Data
@Schema(description = "非遗作品传承人响应")
public class HeritageItemInheritorResponseDTO {

    @Schema(description = "传承人ID")
    private String id;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "称号")
    private String title;

    @Schema(description = "地区")
    private String region;

    @Schema(description = "头像路径")
    private String avatarPath;
}

