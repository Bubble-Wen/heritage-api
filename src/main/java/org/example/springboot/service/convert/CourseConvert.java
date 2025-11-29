package org.example.springboot.service.convert;

import org.example.springboot.dto.command.CourseCreateCommandDTO;
import org.example.springboot.dto.command.CourseUpdateCommandDTO;
import org.example.springboot.dto.response.CourseDetailResponseDTO;
import org.example.springboot.dto.response.CourseResponseDTO;
import org.example.springboot.entity.Course;
import org.springframework.util.StringUtils;

/**
 * 课程DTO转换工具类
 * @author system
 */
public class CourseConvert {

    /**
     * 创建命令DTO转实体
     */
    public static Course createCommandToEntity(CourseCreateCommandDTO createDTO) {
        return Course.builder()
                .id(createDTO.getId()) // 使用前端传来的UUID
                .title(createDTO.getTitle())
                .level(createDTO.getLevel())
                .description(createDTO.getDescription())
                .status(createDTO.getStatus() != null ? createDTO.getStatus() : 0) // 默认草稿状态
                .coverFileId(createDTO.getCoverFileId())
                // createTime 和 updateTime 由 MyBatis-Plus 自动填充，无需手动设置
                .build();
    }

    /**
     * 实体转列表响应DTO
     */
    public static CourseResponseDTO entityToResponse(Course course) {
        CourseResponseDTO response = new CourseResponseDTO();
        response.setId(course.getId());
        response.setTitle(course.getTitle());
        response.setLevel(course.getLevel());
        response.setStatus(course.getStatus());
        response.setStatusName(course.getStatusDisplayName());
        response.setCoverFileId(course.getCoverFileId());
        response.setCreateTime(course.getCreateTime());
        response.setUpdateTime(course.getUpdateTime());
        return response;
    }

    /**
     * 实体转详情响应DTO
     */
    public static CourseDetailResponseDTO entityToDetailResponse(Course course) {
        CourseDetailResponseDTO response = new CourseDetailResponseDTO();
        response.setId(course.getId());
        response.setTitle(course.getTitle());
        response.setLevel(course.getLevel());
        response.setDescription(course.getDescription());
        response.setStatus(course.getStatus());
        response.setStatusName(course.getStatusDisplayName());
        response.setCoverFileId(course.getCoverFileId());
        response.setCreateTime(course.getCreateTime());
        response.setUpdateTime(course.getUpdateTime());
        return response;
    }

    /**
     * 应用更新到实体
     */
    public static void applyUpdateToEntity(Course course, CourseUpdateCommandDTO updateDTO) {
        if (StringUtils.hasText(updateDTO.getTitle())) {
            course.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getLevel() != null) {
            course.setLevel(updateDTO.getLevel());
        }
        if (updateDTO.getDescription() != null) {
            course.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getStatus() != null) {
            course.setStatus(updateDTO.getStatus());
        }
        if (updateDTO.getCoverFileId() != null) {
            course.setCoverFileId(updateDTO.getCoverFileId());
        }
        // updateTime 由 MyBatis-Plus 自动填充
    }
}


