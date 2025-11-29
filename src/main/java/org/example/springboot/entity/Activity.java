package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 活动实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("activity")
@Schema(description = "活动实体类")
public class Activity {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "活动ID（UUID格式）")
    @Size(max = 50, message = "ID长度不能超过50个字符")
    private String id;

    @Schema(description = "活动标题")
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    @Schema(description = "活动类型：展演/展览/培训/比赛")
    @NotBlank(message = "活动类型不能为空")
    @Size(max = 50, message = "活动类型长度不能超过50个字符")
    private String type;

    @Schema(description = "活动开始时间")
    @TableField("start_time")
    private LocalDateTime startTime;

    @Schema(description = "活动结束时间")
    @TableField("end_time")
    private LocalDateTime endTime;

    @Schema(description = "活动地点")
    @Size(max = 200, message = "地点长度不能超过200个字符")
    private String location;

    @Schema(description = "活动描述")
    private String description;

    @Schema(description = "活动状态：0草稿 1报名中 2进行中 3已结束")
    private Integer status;

    @Schema(description = "封面文件ID")
    @TableField("cover_file_id")
    private Long coverFileId;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否为草稿状态
     */
    public boolean isDraft() {
        return this.status != null && this.status == 0;
    }

    /**
     * 是否为报名中状态
     */
    public boolean isSigningUp() {
        return this.status != null && this.status == 1;
    }

    /**
     * 是否为进行中状态
     */
    public boolean isInProgress() {
        return this.status != null && this.status == 2;
    }

    /**
     * 是否为已结束状态
     */
    public boolean isFinished() {
        return this.status != null && this.status == 3;
    }

    /**
     * 是否可以报名
     */
    public boolean canSignup() {
        return isSigningUp();
    }

    /**
     * 是否可以签到
     */
    public boolean canCheckIn() {
        return isInProgress();
    }

    /**
     * 获取状态显示名称
     */
    public String getStatusDisplayName() {
        if (this.status == null) {
            return "未知";
        }
        return switch (this.status) {
            case 0 -> "草稿";
            case 1 -> "报名中";
            case 2 -> "进行中";
            case 3 -> "已结束";
            default -> "未知";
        };
    }

    /**
     * 检查活动时间是否有效
     */
    public boolean isValidTimeRange() {
        if (startTime == null || endTime == null) {
            return false;
        }
        return startTime.isBefore(endTime);
    }

    /**
     * 检查活动是否已开始
     */
    public boolean hasStarted() {
        return startTime != null && LocalDateTime.now().isAfter(startTime);
    }

    /**
     * 检查活动是否已结束
     */
    public boolean hasEnded() {
        return endTime != null && LocalDateTime.now().isAfter(endTime);
    }
}