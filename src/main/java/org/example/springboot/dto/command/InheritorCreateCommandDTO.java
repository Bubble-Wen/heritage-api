package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 传承人创建命令DTO
 * @author system
 */
@Data
@Schema(description = "传承人创建命令")
public class InheritorCreateCommandDTO {

    @Schema(description = "传承人ID（前端UUID预生成）")
    private String id;

    @Schema(description = "姓名")
    @NotBlank(message = "姓名不能为空")
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

