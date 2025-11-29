package org.example.springboot.service.convert;

import org.example.springboot.dto.command.ActivitySignupCreateCommandDTO;
import org.example.springboot.dto.response.ActivitySignupResponseDTO;
import org.example.springboot.entity.ActivitySignup;

/**
 * 活动报名DTO转换工具类
 * @author system
 */
public class ActivitySignupConvert {

    /**
     * 创建命令DTO转实体
     */
    public static ActivitySignup createCommandToEntity(ActivitySignupCreateCommandDTO createDTO) {
        return ActivitySignup.builder()
                .activityId(createDTO.getActivityId())
                .userId(createDTO.getUserId())
                .status(0) // 默认待审状态
                // createTime 由 MyBatis-Plus 自动填充
                .build();
    }

    /**
     * 实体转响应DTO
     */
    public static ActivitySignupResponseDTO entityToResponse(ActivitySignup signup) {
        ActivitySignupResponseDTO response = new ActivitySignupResponseDTO();
        response.setId(signup.getId());
        response.setActivityId(signup.getActivityId());
        response.setUserId(signup.getUserId());
        response.setStatus(signup.getStatus());
        response.setStatusName(signup.getStatusDisplayName());
        response.setCreateTime(signup.getCreateTime());
        return response;
    }
}


