package org.example.springboot.service.convert;

import org.example.springboot.dto.command.HeritageItemCreateCommandDTO;
import org.example.springboot.dto.command.HeritageItemUpdateCommandDTO;
import org.example.springboot.dto.response.HeritageItemDetailResponseDTO;
import org.example.springboot.dto.response.HeritageItemMediaResponseDTO;
import org.example.springboot.entity.HeritageItem;

import org.example.springboot.entity.SysFileInfo;
import org.example.springboot.enums.HeritageItemStatus;
import org.example.springboot.util.DateUtils;
import org.example.springboot.util.JwtTokenUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 非遗作品DTO转换工具类
 * @author system
 */
public class HeritageItemConvert {

    /**
     * 创建命令DTO转实体
     */
    public static HeritageItem createCommandToEntity(HeritageItemCreateCommandDTO createDTO, String creatorId) {
        // 确定最终状态：管理员创建的非草稿作品可以直接发布
        Integer finalStatus = determineFinalStatus(createDTO.getStatus());
        
        HeritageItem item = HeritageItem.builder()
                .id(createDTO.getId()) // 使用前端传来的UUID
                .title(createDTO.getTitle())
                .category(createDTO.getCategory())
                .region(createDTO.getRegion())
                .summary(createDTO.getSummary())
                .description(createDTO.getDescription())
                .status(finalStatus)
                .creatorId(creatorId)
                // createTime 和 updateTime 由 MyBatis-Plus 自动填充，无需手动设置
                .build();

        // 如果状态为已发布，设置发布时间
        if (HeritageItemStatus.PUBLISHED.getCode().equals(item.getStatus())) {
            item.setPublishTime(LocalDateTime.now());
        }

        return item;
    }

    /**
     * 确定作品的最终状态
     * 管理员创建的非草稿作品可以直接发布，跳过审核
     * 
     * 业务规则：
     * 1. 未指定状态 -> 草稿
     * 2. 指定草稿状态 -> 保持草稿
     * 3. 管理员指定待审/已发布 -> 直接发布
     * 4. 普通用户指定待审/已发布 -> 待审状态
     * 
     * @param requestedStatus 前端请求的状态
     * @return 最终确定的状态
     */
    private static Integer determineFinalStatus(Integer requestedStatus) {
        // 如果没有指定状态，默认为草稿
        if (requestedStatus == null) {
            return HeritageItemStatus.DRAFT.getCode();
        }

        // 如果是草稿状态，保持不变
        if (HeritageItemStatus.DRAFT.getCode().equals(requestedStatus)) {
            return requestedStatus;
        }

        // 检查当前用户是否为管理员
        boolean isAdmin = JwtTokenUtils.isAdmin();
        
        // 管理员创建的非草稿作品直接发布
        if (isAdmin && (HeritageItemStatus.PENDING.getCode().equals(requestedStatus) 
                       || HeritageItemStatus.PUBLISHED.getCode().equals(requestedStatus))) {
            return HeritageItemStatus.PUBLISHED.getCode();
        }

        // 普通用户创建的非草稿作品设为待审状态
        if (!isAdmin && (HeritageItemStatus.PENDING.getCode().equals(requestedStatus) 
                        || HeritageItemStatus.PUBLISHED.getCode().equals(requestedStatus))) {
            return HeritageItemStatus.PENDING.getCode();
        }

        // 其他情况保持原状态
        return requestedStatus;
    }

    /**
     * 实体转详情响应DTO
     */
    public static HeritageItemDetailResponseDTO entityToDetailResponse(HeritageItem item) {
        if (item == null) {
            return null;
        }

        HeritageItemDetailResponseDTO response = new HeritageItemDetailResponseDTO();
        response.setId(item.getId());
        response.setTitle(item.getTitle());
        response.setCategory(item.getCategory());
        response.setRegion(item.getRegion());
        response.setSummary(item.getSummary());
        response.setDescription(item.getDescription());
        response.setStatus(item.getStatus());
        response.setStatusName(item.getStatusDisplayName());
        response.setCreatorId(item.getCreatorId());
        response.setCoverFileId(item.getCoverFileId());
        response.setPublishTime(item.getPublishTime());
        response.setCreateTime(item.getCreateTime());
        response.setUpdateTime(item.getUpdateTime());

        return response;
    }

    /**
     * 应用更新到实体
     */
    public static void applyUpdateToEntity(HeritageItem item, HeritageItemUpdateCommandDTO updateDTO) {
        if (updateDTO.getTitle() != null) {
            item.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getCategory() != null) {
            item.setCategory(updateDTO.getCategory());
        }
        if (updateDTO.getRegion() != null) {
            item.setRegion(updateDTO.getRegion());
        }
        if (updateDTO.getSummary() != null) {
            item.setSummary(updateDTO.getSummary());
        }
        if (updateDTO.getDescription() != null) {
            item.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getStatus() != null) {
            Integer oldStatus = item.getStatus();
            item.setStatus(updateDTO.getStatus());
            
            // 如果从非发布状态变为发布状态，设置发布时间
            if (!HeritageItemStatus.PUBLISHED.getCode().equals(oldStatus) 
                && HeritageItemStatus.PUBLISHED.getCode().equals(updateDTO.getStatus())) {
                item.setPublishTime(LocalDateTime.now());
            }
        }
        // updateTime 由 MyBatis-Plus 自动填充，无需手动设置
    }

    // 旧的媒体转换方法已移除，现在直接使用文件信息转换

    /**
     * 直接从文件信息列表转换为媒体响应DTO列表
     */
    public static List<HeritageItemMediaResponseDTO> fileInfoListToMediaResponseList(
            List<SysFileInfo> fileInfoList) {
        if (fileInfoList == null || fileInfoList.isEmpty()) {
            return List.of();
        }

        return fileInfoList.stream()
                .map(HeritageItemConvert::fileInfoToMediaResponse)
                .collect(Collectors.toList());
    }

    /**
     * 文件信息转媒体响应DTO
     */
    public static HeritageItemMediaResponseDTO fileInfoToMediaResponse(SysFileInfo fileInfo) {
        if (fileInfo == null) {
            return null;
        }

        HeritageItemMediaResponseDTO response = new HeritageItemMediaResponseDTO();
        response.setId(fileInfo.getId()); // 使用文件ID作为媒体ID
        response.setFileId(fileInfo.getId());
        response.setFilePath(fileInfo.getFilePath());
        response.setOriginalName(fileInfo.getOriginalName());
        response.setFileSize(fileInfo.getFileSize());
        response.setType(fileInfo.getFileType());
        response.setSort(0); // 默认排序，可以根据需要调整

        return response;
    }

    /**
     * 填充创建人信息
     */
    public static void fillCreatorInfo(HeritageItemDetailResponseDTO response, String creatorName) {
        if (response != null) {
            response.setCreatorName(creatorName);
        }
    }

    /**
     * 批量填充创建人信息
     */
    public static void fillCreatorInfoBatch(List<HeritageItemDetailResponseDTO> responseList, 
                                          java.util.Map<String, String> creatorNameMap) {
        if (responseList != null && creatorNameMap != null) {
            responseList.forEach(response -> {
                String creatorName = creatorNameMap.get(response.getCreatorId());
                if (creatorName != null) {
                    response.setCreatorName(creatorName);
                }
            });
        }
    }
}

