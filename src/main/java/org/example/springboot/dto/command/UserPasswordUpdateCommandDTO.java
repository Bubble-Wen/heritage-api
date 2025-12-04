package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户密码更新命令DTO
 * @author system
 */
@Data
@Schema(description = "用户密码更新命令")
public class UserPasswordUpdateCommandDTO {

    @Schema(description = "旧密码", example = "oldPassword123", required = true)
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @Schema(description = "新密码", example = "newPassword123", required = true)
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度必须在6-20个字符之间")
    private String newPassword;
}
