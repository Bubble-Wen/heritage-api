package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户信息更新命令DTO
 * @author system
 */
@Data
@Schema(description = "用户信息更新命令")
public class UserUpdateCommandDTO {

    @Schema(description = "邮箱", example = "test@drone.com")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    @Schema(description = "姓名", example = "测试用户")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String name;

    @Schema(description = "头像", example = "/avatars/user.jpg")
    @Size(max = 200, message = "头像路径长度不能超过200个字符")
    private String avatar;

    @Schema(description = "手机号", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "性别", example = "男", allowableValues = {"男", "女"})
    private String sex;

    @Schema(description = "用户类型", example = "USER", allowableValues = {"USER", "ADMIN"})
    private String userType;

    @Schema(description = "用户状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;
}
