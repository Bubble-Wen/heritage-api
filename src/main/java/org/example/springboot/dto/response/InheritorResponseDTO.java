package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 传承人响应DTO
 * @author system
 */
@Data
@Schema(description = "传承人响应")
public class InheritorResponseDTO {

    @Schema(description = "传承人ID")
    private String id;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "称号")
    private String title;

    @Schema(description = "地区")
    private String region;

    @Schema(description = "简介")
    private String bio;

    @Schema(description = "头像文件ID")
    private Long avatarFileId;

    @Schema(description = "头像路径")
    private String avatarPath;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

