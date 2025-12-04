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

import java.time.LocalDateTime;

/**
 * 活动报名实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("activity_signup")
@Schema(description = "活动报名实体类")
public class ActivitySignup {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "活动ID")
    @TableField("activity_id")
    private String activityId;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "状态 0待审 1通过 2拒绝 3已签到")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 是否为待审状态
     */
    public boolean isPending() {
        return this.status != null && this.status == 0;
    }

    /**
     * 是否为通过状态
     */
    public boolean isApproved() {
        return this.status != null && this.status == 1;
    }

    /**
     * 是否为拒绝状态
     */
    public boolean isRejected() {
        return this.status != null && this.status == 2;
    }

    /**
     * 是否为已签到状态
     */
    public boolean isCheckedIn() {
        return this.status != null && this.status == 3;
    }

    /**
     * 获取状态显示名称
     */
    public String getStatusDisplayName() {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "待审";
            case 1:
                return "通过";
            case 2:
                return "拒绝";
            case 3:
                return "已签到";
            default:
                return "未知";
        }
    }

    /**
     * 是否可以签到
     */
    public boolean canCheckIn() {
        return isApproved() && !isCheckedIn();
    }
}

