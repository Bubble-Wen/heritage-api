package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 传承人更新命令DTO
 * @author system
 */
@Data
@Schema(description = "传承人更新命令")
public class InheritorUpdateCommandDTO {

    @Schema(description = "姓名")
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    private String name;

    @Schema(description = "称号")
    @Size(max = 100, message = "称号长度不能超过100个字符")
    private String title;

    @Schema(description = "地区")
    @Size(max = 100, message = "地区长度不能超过100个字符")
    private String region;

    @Schema(description = "简介")
    private String bio;

    @Schema(description = "头像文件ID")
    private Long avatarFileId;
}

