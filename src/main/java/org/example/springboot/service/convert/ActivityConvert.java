package org.example.springboot.service.convert;

import org.example.springboot.dto.command.ActivityCreateCommandDTO;
import org.example.springboot.dto.command.ActivityUpdateCommandDTO;
import org.example.springboot.dto.response.ActivityDetailResponseDTO;
import org.example.springboot.dto.response.ActivityResponseDTO;
import org.example.springboot.entity.Activity;
import org.springframework.util.StringUtils;

/**
 * 活动DTO转换工具类
 * @author system
 */
public class ActivityConvert {

    /**
     * 创建命令DTO转实体
     */
    public static Activity createCommandToEntity(ActivityCreateCommandDTO createDTO) {
        return Activity.builder()
                .id(createDTO.getId()) // 使用前端传来的UUID
                .title(createDTO.getTitle())
                .type(createDTO.getType())
                .startTime(createDTO.getStartTime())
                .endTime(createDTO.getEndTime())
                .location(createDTO.getLocation())
                .description(createDTO.getDescription())
                .status(createDTO.getStatus() != null ? createDTO.getStatus() : 0) // 默认草稿状态
                .coverFileId(createDTO.getCoverFileId())
                // createTime 和 updateTime 由 MyBatis-Plus 自动填充，无需手动设置
                .build();
    }

    /**
     * 实体转列表响应DTO
     */
    public static ActivityResponseDTO entityToResponse(Activity activity) {
        ActivityResponseDTO response = new ActivityResponseDTO();
        response.setId(activity.getId());
        response.setTitle(activity.getTitle());
        response.setType(activity.getType());
        response.setStartTime(activity.getStartTime());
        response.setEndTime(activity.getEndTime());
        response.setLocation(activity.getLocation());
        response.setStatus(activity.getStatus());
        response.setStatusName(activity.getStatusDisplayName());
        response.setCoverFileId(activity.getCoverFileId());
        response.setCreateTime(activity.getCreateTime());
        response.setUpdateTime(activity.getUpdateTime());
        return response;
    }

    /**
     * 实体转详情响应DTO
     */
    public static ActivityDetailResponseDTO entityToDetailResponse(Activity activity) {
        ActivityDetailResponseDTO response = new ActivityDetailResponseDTO();
        response.setId(activity.getId());
        response.setTitle(activity.getTitle());
        response.setType(activity.getType());
        response.setStartTime(activity.getStartTime());
        response.setEndTime(activity.getEndTime());
        response.setLocation(activity.getLocation());
        response.setDescription(activity.getDescription());
        response.setStatus(activity.getStatus());
        response.setStatusName(activity.getStatusDisplayName());
        response.setCoverFileId(activity.getCoverFileId());
        response.setCreateTime(activity.getCreateTime());
        response.setUpdateTime(activity.getUpdateTime());
        return response;
    }

    /**
     * 应用更新到实体
     */
    public static void applyUpdateToEntity(Activity activity, ActivityUpdateCommandDTO updateDTO) {
        if (StringUtils.hasText(updateDTO.getTitle())) {
            activity.setTitle(updateDTO.getTitle());
        }
        if (StringUtils.hasText(updateDTO.getType())) {
            activity.setType(updateDTO.getType());
        }
        if (updateDTO.getStartTime() != null) {
            activity.setStartTime(updateDTO.getStartTime());
        }
        if (updateDTO.getEndTime() != null) {
            activity.setEndTime(updateDTO.getEndTime());
        }
        if (updateDTO.getLocation() != null) {
            activity.setLocation(updateDTO.getLocation());
        }
        if (updateDTO.getDescription() != null) {
            activity.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getStatus() != null) {
            activity.setStatus(updateDTO.getStatus());
        }
        if (updateDTO.getCoverFileId() != null) {
            activity.setCoverFileId(updateDTO.getCoverFileId());
        }
        // updateTime 由 MyBatis-Plus 自动填充
    }
}


