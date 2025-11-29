package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 传承人实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("inheritor")
@Schema(description = "传承人实体类")
public class Inheritor {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "传承人ID(支持数字ID和UUID)")
    @Size(max = 50, message = "ID长度不能超过50个字符")
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
    @TableField("avatar_file_id")
    private Long avatarFileId;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 获取业务标识（直接返回ID）
     */
    public String getBusinessId() {
        return id;
    }

    /**
     * 是否使用UUID格式的ID
     */
    public boolean isUsingUuid() {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        // 简单的UUID格式检查：包含4个连字符的36字符字符串
        return id.length() == 36 && id.chars().filter(ch -> ch == '-').count() == 4;
    }

    /**
     * 是否使用数字格式的ID
     */
    public boolean isUsingNumericId() {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        try {
            Long.parseLong(id);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 获取显示名称（姓名 + 称号）
     */
    public String getDisplayName() {
        if (title != null && !title.trim().isEmpty()) {
            return name + "（" + title + "）";
        }
        return name;
    }

    /**
     * 获取简短简介（最多50字）
     */
    public String getShortBio() {
        if (bio == null || bio.trim().isEmpty()) {
            return "";
        }
        if (bio.length() <= 50) {
            return bio;
        }
        return bio.substring(0, 50) + "...";
    }
}

