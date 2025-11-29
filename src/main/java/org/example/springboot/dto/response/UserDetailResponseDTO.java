package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户详情响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户详情响应")
public class UserDetailResponseDTO {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "邮箱", example = "admin@drone.com")
    private String email;

    @Schema(description = "姓名", example = "系统管理员")
    private String name;

    @Schema(description = "头像", example = "/avatars/admin.jpg")
    private String avatar;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "性别", example = "男")
    private String sex;

    @Schema(description = "用户类型", example = "ADMIN")
    private String userType;

    @Schema(description = "用户类型显示名称", example = "管理员")
    private String userTypeDisplayName;

    @Schema(description = "用户状态", example = "1")
    private Integer status;

    @Schema(description = "用户状态显示名称", example = "正常")
    private String statusDisplayName;

    @Schema(description = "显示名称", example = "系统管理员")
    private String displayName;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
