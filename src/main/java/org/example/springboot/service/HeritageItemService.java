package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.springboot.entity.HeritageItem;
import org.example.springboot.entity.SysFileInfo;
import org.example.springboot.entity.User;
import org.example.springboot.mapper.HeritageItemMapper;
import org.example.springboot.mapper.SysFileInfoMapper;
import org.example.springboot.mapper.UserMapper;
import org.example.springboot.dto.command.HeritageItemCreateCommandDTO;
import org.example.springboot.dto.command.HeritageItemUpdateCommandDTO;
import org.example.springboot.dto.query.HeritageItemListQueryDTO;
import org.example.springboot.dto.response.HeritageItemDetailResponseDTO;
import org.example.springboot.enums.HeritageItemStatus;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.service.convert.HeritageItemConvert;
import org.example.springboot.util.JwtTokenUtils;

/**
 * 非遗作品业务逻辑层
 * @author system
 */
@Slf4j
@Service
public class HeritageItemService {

    @Resource
    private HeritageItemMapper heritageItemMapper;



    @Resource
    private SysFileInfoMapper sysFileInfoMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 创建非遗作品
     * @param createDTO 创建命令
     * @return 作品详情
     */
    @Transactional(rollbackFor = Exception.class)
    public HeritageItemDetailResponseDTO createHeritageItem(HeritageItemCreateCommandDTO createDTO) {
        try {
            // 获取当前用户ID
            String currentUserId = JwtTokenUtils.getCurrentUserIdAsString();
            if (!StringUtils.hasText(currentUserId)) {
                throw new BusinessException("用户未登录");
            }

            // 验证UUID是否提供（策略B要求）
            if (!StringUtils.hasText(createDTO.getId())) {
                throw new BusinessException("作品ID不能为空，请使用UUID预生成策略");
            }

            // 验证UUID是否已存在
            HeritageItem existingItem = heritageItemMapper.selectById(createDTO.getId());
            if (existingItem != null) {
                throw new BusinessException("作品ID已存在，请重新生成UUID");
            }

            // 验证状态
            if (createDTO.getStatus() != null && !HeritageItemStatus.isValidCode(createDTO.getStatus())) {
                throw new BusinessException("无效的作品状态");
            }

            // 创建作品实体
            HeritageItem item = HeritageItemConvert.createCommandToEntity(createDTO, currentUserId);
            heritageItemMapper.insert(item);

            // 记录创建日志，包含状态变更信息
            String statusChangeInfo = "";
            if (createDTO.getStatus() != null && !createDTO.getStatus().equals(item.getStatus())) {
                statusChangeInfo = String.format(" (状态从%d调整为%d)", createDTO.getStatus(), item.getStatus());
            }
            log.info("非遗作品创建成功: id={}, title={}, creator={}, status={}{}", 
                    item.getId(), item.getTitle(), currentUserId, item.getStatus(), statusChangeInfo);

            // 查询并返回详情
            return getHeritageItemById(item.getId());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("非遗作品创建失败", e);
            throw new ServiceException("作品创建失败，请稍后重试");
        }
    }

    /**
     * 根据ID获取非遗作品详情
     * @param itemId 作品ID
     * @return 作品详情
     */
    public HeritageItemDetailResponseDTO getHeritageItemById(String itemId) {
        try {
            HeritageItem item = heritageItemMapper.selectById(itemId);
            if (item == null) {
                throw new BusinessException("作品不存在");
            }

            HeritageItemDetailResponseDTO response = HeritageItemConvert.entityToDetailResponse(item);
            
            // 填充关联信息
            fillItemInfo(response);

            return response;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询作品详情失败: itemId={}", itemId, e);
            throw new ServiceException("查询失败，请稍后重试");
        }
    }

    /**
     * 分页查询非遗作品列表
     * @param queryDTO 查询条件
     * @return 作品分页列表
     */
    public Page<HeritageItemDetailResponseDTO> getHeritageItemPage(HeritageItemListQueryDTO queryDTO) {
        try {
            Page<HeritageItem> page = new Page<>(queryDTO.getCurrentPage(), queryDTO.getSize());
            Page<HeritageItem> itemPage = heritageItemMapper.selectPageWithConditions(page, queryDTO);

            // 转换为响应DTO
            Page<HeritageItemDetailResponseDTO> resultPage = new Page<>(
                    itemPage.getCurrent(), itemPage.getSize(), itemPage.getTotal());
            
            List<HeritageItemDetailResponseDTO> records = itemPage.getRecords().stream()
                    .map(HeritageItemConvert::entityToDetailResponse)
                    .collect(Collectors.toList());

            // 批量填充作品信息（包括创建人和封面）
            fillItemInfoBatch(records);

            resultPage.setRecords(records);
            return resultPage;

        } catch (Exception e) {
            log.error("查询作品列表失败", e);
            throw new ServiceException("查询失败，请稍后重试");
        }
    }

    /**
     * 更新非遗作品
     * @param itemId 作品ID
     * @param updateDTO 更新命令
     * @return 更新后的作品详情
     */
    @Transactional(rollbackFor = Exception.class)
    public HeritageItemDetailResponseDTO updateHeritageItem(String itemId, HeritageItemUpdateCommandDTO updateDTO) {
        try {
            HeritageItem item = heritageItemMapper.selectById(itemId);
            if (item == null) {
                throw new BusinessException("作品不存在");
            }

            // 权限检查：只有创建人或管理员可以编辑
            String currentUserId = JwtTokenUtils.getCurrentUserIdAsString();
            if (!item.getCreatorId().equals(currentUserId) && !JwtTokenUtils.isAdmin()) {
                throw new BusinessException("无权限编辑此作品");
            }

            // 状态检查：只有草稿和待审状态可以编辑
            HeritageItemStatus currentStatus = HeritageItemStatus.fromCode(item.getStatus());


            // 验证新状态


            // 应用更新
            HeritageItemConvert.applyUpdateToEntity(item, updateDTO);
            heritageItemMapper.updateById(item);

            log.info("非遗作品更新成功: id={}, title={}", item.getId(), item.getTitle());

            return getHeritageItemById(itemId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("作品更新失败: itemId={}", itemId, e);
            throw new ServiceException("更新失败，请稍后重试");
        }
    }

    /**
     * 删除非遗作品
     * @param itemId 作品ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteHeritageItem(String itemId) {
        try {
            HeritageItem item = heritageItemMapper.selectById(itemId);
            if (item == null) {
                throw new BusinessException("作品不存在");
            }

            // 权限检查：只有创建人或管理员可以删除
            String currentUserId = JwtTokenUtils.getCurrentUserIdAsString();
            if (!item.getCreatorId().equals(currentUserId) && !JwtTokenUtils.isAdmin()) {
                throw new BusinessException("无权限删除此作品");
            }

            // 状态检查：只有草稿和下架状态可以删除
            HeritageItemStatus currentStatus = HeritageItemStatus.fromCode(item.getStatus());
            if (currentStatus != null && !currentStatus.canDelete()) {
                throw new BusinessException("当前状态不允许删除");
            }


            // 删除作品
            heritageItemMapper.deleteById(itemId);

            log.info("非遗作品删除成功: id={}, title={}", itemId, item.getTitle());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("作品删除失败: itemId={}", itemId, e);
            throw new ServiceException("删除失败，请稍后重试");
        }
    }

    /**
     * 发布作品
     * @param itemId 作品ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void publishHeritageItem(String itemId) {
        try {
            HeritageItem item = heritageItemMapper.selectById(itemId);
            if (item == null) {
                throw new BusinessException("作品不存在");
            }

            // 权限检查：只有创建人或管理员可以发布
            String currentUserId = JwtTokenUtils.getCurrentUserIdAsString();
            if (!item.getCreatorId().equals(currentUserId) && !JwtTokenUtils.isAdmin()) {
                throw new BusinessException("无权限发布此作品");
            }

            // 状态检查
            HeritageItemStatus currentStatus = HeritageItemStatus.fromCode(item.getStatus());
            if (currentStatus != null && !currentStatus.canPublish()) {
                throw new BusinessException("当前状态不允许发布");
            }

            // 更新状态为已发布
            item.setStatus(HeritageItemStatus.PUBLISHED.getCode());
            item.setPublishTime(LocalDateTime.now());
            // updateTime 由 MyBatis-Plus 自动填充，无需手动设置
            heritageItemMapper.updateById(item);

            log.info("非遗作品发布成功: id={}, title={}", itemId, item.getTitle());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("作品发布失败: itemId={}", itemId, e);
            throw new ServiceException("发布失败，请稍后重试");
        }
    }

    /**
     * 下架作品
     * @param itemId 作品ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void offlineHeritageItem(String itemId) {
        try {
            HeritageItem item = heritageItemMapper.selectById(itemId);
            if (item == null) {
                throw new BusinessException("作品不存在");
            }

            // 权限检查：只有管理员可以下架
            if (!JwtTokenUtils.isAdmin()) {
                throw new BusinessException("无权限下架此作品");
            }

            // 状态检查
            HeritageItemStatus currentStatus = HeritageItemStatus.fromCode(item.getStatus());
            if (currentStatus != null && !currentStatus.canOffline()) {
                throw new BusinessException("当前状态不允许下架");
            }

            // 更新状态为下架
            item.setStatus(HeritageItemStatus.OFFLINE.getCode());
            // updateTime 由 MyBatis-Plus 自动填充，无需手动设置
            heritageItemMapper.updateById(item);

            log.info("非遗作品下架成功: id={}, title={}", itemId, item.getTitle());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("作品下架失败: itemId={}", itemId, e);
            throw new ServiceException("下架失败，请稍后重试");
        }
    }

    /**
     * 获取热门作品列表
     * @param limit 限制数量
     * @return 作品列表
     */
    public List<HeritageItemDetailResponseDTO> getPopularItems(Integer limit) {
        try {
            List<HeritageItem> items = heritageItemMapper.selectPopularItems(limit != null ? limit : 10);
            List<HeritageItemDetailResponseDTO> result = items.stream()
                    .map(HeritageItemConvert::entityToDetailResponse)
                    .collect(Collectors.toList());

            fillItemInfoBatch(result);
            return result;

        } catch (Exception e) {
            log.error("查询热门作品失败", e);
            throw new ServiceException("查询失败，请稍后重试");
        }
    }

    /**
     * 搜索作品
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 作品列表
     */
    public List<HeritageItemDetailResponseDTO> searchItems(String keyword, Integer limit) {
        try {
            if (!StringUtils.hasText(keyword)) {
                return List.of();
            }

            List<HeritageItem> items = heritageItemMapper.searchByKeyword(keyword, limit != null ? limit : 20);
            List<HeritageItemDetailResponseDTO> result = items.stream()
                    .map(HeritageItemConvert::entityToDetailResponse)
                    .collect(Collectors.toList());

            fillItemInfoBatch(result);
            return result;

        } catch (Exception e) {
            log.error("搜索作品失败: keyword={}", keyword, e);
            throw new ServiceException("搜索失败，请稍后重试");
        }
    }

    /**
     * 填充作品关联信息
     */
    private void fillItemInfo(HeritageItemDetailResponseDTO response) {
        if (response == null) {
            return;
        }

        // 填充创建人信息
        if (StringUtils.hasText(response.getCreatorId())) {
            try {
                Long userId = Long.parseLong(response.getCreatorId());
                User creator = userMapper.selectById(userId);
                if (creator != null) {
                    HeritageItemConvert.fillCreatorInfo(response, creator.getName());
                }
            } catch (NumberFormatException e) {
                log.warn("无效的创建人ID: {}", response.getCreatorId());
            }
        }

        // 填充封面信息 - 从 sys_file_info 表查询 business_field='cover' 的文件
        LambdaQueryWrapper<SysFileInfo> coverQuery = new LambdaQueryWrapper<>();
        coverQuery.eq(SysFileInfo::getBusinessType, "HERITAGE_ITEM")
                .eq(SysFileInfo::getBusinessId, response.getId())
                .eq(SysFileInfo::getBusinessField, "cover")
                .eq(SysFileInfo::getStatus, 1) // 只查询正常状态的文件
                .orderByDesc(SysFileInfo::getCreateTime) // 按创建时间倒序，取最新的封面
                .last("LIMIT 1");
        
        SysFileInfo coverFile = sysFileInfoMapper.selectOne(coverQuery);
        if (coverFile != null) {
            response.setCoverFileId(coverFile.getId());
            response.setCoverImage(coverFile.getFilePath());
        }

        // 填充媒体信息 - 直接从 sys_file_info 表查询 business_field='media' 的文件
        LambdaQueryWrapper<SysFileInfo> fileQuery = new LambdaQueryWrapper<>();
        fileQuery.eq(SysFileInfo::getBusinessType, "HERITAGE_ITEM")
                .eq(SysFileInfo::getBusinessId, response.getId())
                .eq(SysFileInfo::getBusinessField, "media")
                .eq(SysFileInfo::getStatus, 1) // 只查询正常状态的文件
                .orderByAsc(SysFileInfo::getCreateTime); // 按创建时间排序
        
        List<SysFileInfo> fileInfoList = sysFileInfoMapper.selectList(fileQuery);
        if (!fileInfoList.isEmpty()) {
            response.setMediaList(HeritageItemConvert.fileInfoListToMediaResponseList(fileInfoList));
        }
    }

    /**
     * 批量填充作品信息（创建人和封面）
     */
    private void fillItemInfoBatch(List<HeritageItemDetailResponseDTO> responseList) {
        if (responseList == null || responseList.isEmpty()) {
            return;
        }

        // 1. 填充创建人信息
        List<String> creatorIds = responseList.stream()
                .map(HeritageItemDetailResponseDTO::getCreatorId)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());

        if (!creatorIds.isEmpty()) {
            List<Long> userIds = creatorIds.stream()
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            
            LambdaQueryWrapper<User> userQuery = new LambdaQueryWrapper<>();
            userQuery.in(User::getId, userIds);
            List<User> users = userMapper.selectList(userQuery);

            Map<String, String> creatorNameMap = users.stream()
                    .collect(Collectors.toMap(
                            user -> String.valueOf(user.getId()),
                            User::getName,
                            (existing, replacement) -> existing
                    ));

            HeritageItemConvert.fillCreatorInfoBatch(responseList, creatorNameMap);
        }

        // 2. 批量填充封面信息
        List<String> itemIds = responseList.stream()
                .map(HeritageItemDetailResponseDTO::getId)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());

        if (!itemIds.isEmpty()) {
            // 批量查询所有作品的封面文件
            LambdaQueryWrapper<SysFileInfo> coverQuery = new LambdaQueryWrapper<>();
            coverQuery.eq(SysFileInfo::getBusinessType, "HERITAGE_ITEM")
                    .in(SysFileInfo::getBusinessId, itemIds)
                    .eq(SysFileInfo::getBusinessField, "cover")
                    .eq(SysFileInfo::getStatus, 1)
                    .orderByDesc(SysFileInfo::getCreateTime);
            
            List<SysFileInfo> coverFiles = sysFileInfoMapper.selectList(coverQuery);
            
            // 为每个作品ID保留最新的封面文件
            Map<String, SysFileInfo> coverFileMap = coverFiles.stream()
                    .collect(Collectors.toMap(
                            SysFileInfo::getBusinessId,
                            file -> file,
                            (existing, replacement) -> existing.getCreateTime().isAfter(replacement.getCreateTime()) 
                                    ? existing : replacement
                    ));

            // 填充封面信息
            responseList.forEach(response -> {
                SysFileInfo coverFile = coverFileMap.get(response.getId());
                if (coverFile != null) {
                    response.setCoverFileId(coverFile.getId());
                    response.setCoverImage(coverFile.getFilePath());
                }
            });
        }
    }
}
