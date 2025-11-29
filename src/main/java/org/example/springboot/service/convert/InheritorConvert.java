package org.example.springboot.service.convert;

import org.example.springboot.dto.command.InheritorCreateCommandDTO;
import org.example.springboot.dto.command.InheritorUpdateCommandDTO;
import org.example.springboot.dto.response.InheritorDetailResponseDTO;
import org.example.springboot.dto.response.InheritorItemResponseDTO;
import org.example.springboot.dto.response.InheritorResponseDTO;
import org.example.springboot.entity.HeritageItem;
import org.example.springboot.entity.Inheritor;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 传承人DTO转换工具类
 * @author system
 */
public class InheritorConvert {

    /**
     * 创建命令DTO转实体
     */
    public static Inheritor createCommandToEntity(InheritorCreateCommandDTO createDTO) {
        return Inheritor.builder()
                .id(createDTO.getId()) // 使用前端传来的UUID
                .name(createDTO.getName())
                .title(createDTO.getTitle())
                .region(createDTO.getRegion())
                .bio(createDTO.getBio())
                .avatarFileId(createDTO.getAvatarFileId())
                // createTime 和 updateTime 由 MyBatis-Plus 自动填充，无需手动设置
                .build();
    }

    /**
     * 实体转响应DTO
     */
    public static InheritorResponseDTO entityToResponse(Inheritor inheritor) {
        InheritorResponseDTO response = new InheritorResponseDTO();
        response.setId(inheritor.getId());
        response.setName(inheritor.getName());
        response.setTitle(inheritor.getTitle());
        response.setRegion(inheritor.getRegion());
        response.setBio(inheritor.getBio());
        response.setAvatarFileId(inheritor.getAvatarFileId());
        response.setCreateTime(inheritor.getCreateTime());
        response.setUpdateTime(inheritor.getUpdateTime());
        return response;
    }

    /**
     * 实体转详情响应DTO
     */
    public static InheritorDetailResponseDTO entityToDetailResponse(Inheritor inheritor) {
        InheritorDetailResponseDTO response = new InheritorDetailResponseDTO();
        response.setId(inheritor.getId());
        response.setName(inheritor.getName());
        response.setTitle(inheritor.getTitle());
        response.setRegion(inheritor.getRegion());
        response.setBio(inheritor.getBio());
        response.setAvatarFileId(inheritor.getAvatarFileId());
        response.setCreateTime(inheritor.getCreateTime());
        response.setUpdateTime(inheritor.getUpdateTime());
        return response;
    }

    /**
     * 应用更新到实体
     */
    public static void applyUpdateToEntity(Inheritor inheritor, InheritorUpdateCommandDTO updateDTO) {
        if (StringUtils.hasText(updateDTO.getName())) {
            inheritor.setName(updateDTO.getName());
        }
        if (updateDTO.getTitle() != null) {
            inheritor.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getRegion() != null) {
            inheritor.setRegion(updateDTO.getRegion());
        }
        if (updateDTO.getBio() != null) {
            inheritor.setBio(updateDTO.getBio());
        }
        if (updateDTO.getAvatarFileId() != null) {
            inheritor.setAvatarFileId(updateDTO.getAvatarFileId());
        }
        // updateTime 由 MyBatis-Plus 自动填充，无需手动设置
    }

    /**
     * 填充头像路径
     */
    public static void fillAvatarPath(InheritorResponseDTO response, String avatarPath) {
        response.setAvatarPath(avatarPath);
    }

    /**
     * 批量填充头像路径
     */
    public static void fillAvatarPathBatch(List<InheritorResponseDTO> responseList, Map<Long, String> avatarPathMap) {
        if (responseList == null || responseList.isEmpty() || avatarPathMap == null) {
            return;
        }
        responseList.forEach(response -> {
            if (response.getAvatarFileId() != null) {
                response.setAvatarPath(avatarPathMap.get(response.getAvatarFileId()));
            }
        });
    }

    /**
     * 填充头像路径（详情DTO）
     */
    public static void fillAvatarPathForDetail(InheritorDetailResponseDTO response, String avatarPath) {
        response.setAvatarPath(avatarPath);
    }

    /**
     * 非遗作品实体转传承人关联作品响应DTO
     */
    public static InheritorItemResponseDTO heritageItemToInheritorItemResponse(HeritageItem item) {
        InheritorItemResponseDTO response = new InheritorItemResponseDTO();
        response.setId(item.getId());
        response.setTitle(item.getTitle());
        response.setCategory(item.getCategory());
        response.setRegion(item.getRegion());
        response.setSummary(item.getSummary());
        response.setStatus(item.getStatus());
        response.setStatusName(item.getStatusDisplayName());
        return response;
    }

    /**
     * 批量转换非遗作品为传承人关联作品响应DTO
     */
    public static List<InheritorItemResponseDTO> heritageItemListToInheritorItemResponseList(List<HeritageItem> items) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        return items.stream()
                .map(InheritorConvert::heritageItemToInheritorItemResponse)
                .collect(Collectors.toList());
    }
}

