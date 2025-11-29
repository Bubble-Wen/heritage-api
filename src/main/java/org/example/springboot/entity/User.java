package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
@Schema(description = "用户实体类")
public class User {

    @TableId(type = IdType.AUTO)
    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3到50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6到100个字符之间")
    private String password;

    @Schema(description = "邮箱")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    @Schema(description = "手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "用户类型")
    @TableField("user_type")
    private String userType;

    @Schema(description = "姓名")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String name;

    @Schema(description = "头像")
    @Size(max = 200, message = "头像路径长度不能超过200个字符")
    private String avatar;

    @Schema(description = "状态(0:禁用,1:正常)")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @Schema(description = "性别")
    private String sex;

    /**
     * 是否为管理员
     */
    public boolean isAdmin() {
        return "ADMIN".equals(this.userType);
    }

    /**
     * 是否为普通用户
     */
    public boolean isUser() {
        return "USER".equals(this.userType);
    }

    /**
     * 是否为正常状态
     */
    public boolean isActive() {
        return this.status != null && this.status == 1;
    }

    /**
     * 是否被禁用
     */
    public boolean isDisabled() {
        return this.status != null && this.status == 0;
    }

    /**
     * 获取用户类型显示名称
     */
    public String getUserTypeDisplayName() {
        if (userType == null) {
            return "未知";
        }
        switch (userType) {
            case "ADMIN":
                return "管理员";
            case "USER":
                return "普通用户";
            default:
                return "未知";
        }
    }

    /**
     * 获取用户状态显示名称
     */
    public String getStatusDisplayName() {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 1:
                return "正常";
            case 0:
                return "禁用";
            default:
                return "未知";
        }
    }

    /**
     * 获取显示名称（优先使用姓名，其次用户名）
     */
    public String getDisplayName() {
        if (name != null && !name.trim().isEmpty()) {
            return name;
        }
        return username;
    }
}
