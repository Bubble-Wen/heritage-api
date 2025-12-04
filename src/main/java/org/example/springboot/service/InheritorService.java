package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.springboot.entity.Inheritor;
import org.example.springboot.entity.InheritorItem;
import org.example.springboot.entity.HeritageItem;
import org.example.springboot.entity.SysFileInfo;
import org.example.springboot.mapper.InheritorMapper;
import org.example.springboot.mapper.InheritorItemMapper;
import org.example.springboot.mapper.HeritageItemMapper;
import org.example.springboot.mapper.SysFileInfoMapper;
import org.example.springboot.dto.command.InheritorCreateCommandDTO;
import org.example.springboot.dto.command.InheritorUpdateCommandDTO;
import org.example.springboot.dto.response.InheritorResponseDTO;
import org.example.springboot.dto.response.InheritorDetailResponseDTO;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.service.convert.InheritorConvert;

/**
 * 传承人业务逻辑层
 * @author system
 */
@Slf4j
@Service
public class InheritorService {

    @Resource
    private InheritorMapper inheritorMapper;

    @Resource
    private InheritorItemMapper inheritorItemMapper;

    @Resource
    private HeritageItemMapper heritageItemMapper;

    @Resource
    private SysFileInfoMapper sysFileInfoMapper;

    /**
     * 创建传承人
     * @param createDTO 创建命令
     * @return 传承人详情
     */
    @Transactional(rollbackFor = Exception.class)
    public InheritorDetailResponseDTO createInheritor(InheritorCreateCommandDTO createDTO) {
        try {
            // 验证UUID是否提供（策略B要求）
            if (!StringUtils.hasText(createDTO.getId())) {
                throw new BusinessException("传承人ID不能为空，请使用UUID预生成策略");
            }

            // 验证UUID是否已存在
            Inheritor existingInheritor = inheritorMapper.selectById(createDTO.getId());
            if (existingInheritor != null) {
                throw new BusinessException("传承人ID已存在，请重新生成UUID");
            }

            // 创建传承人实体
            Inheritor inheritor = InheritorConvert.createCommandToEntity(createDTO);
            inheritorMapper.insert(inheritor);

            log.info("传承人创建成功: id={}, name={}", inheritor.getId(), inheritor.getName());

            // 查询并返回详情
            return getInheritorById(inheritor.getId());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("传承人创建失败", e);
            throw new ServiceException("传承人创建失败，请稍后重试");
        }
    }

    /**
     * 根据ID获取传承人详情
     * @param inheritorId 传承人ID
     * @return 传承人详情
     */
    public InheritorDetailResponseDTO getInheritorById(String inheritorId) {
        try {
            Inheritor inheritor = inheritorMapper.selectById(inheritorId);
            if (inheritor == null) {
                throw new BusinessException("传承人不存在");
            }

            InheritorDetailResponseDTO response = InheritorConvert.entityToDetailResponse(inheritor);
            
            // 填充关联信息
            fillInheritorInfo(response);

            return response;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询传承人详情失败: inheritorId={}", inheritorId, e);
            throw new ServiceException("查询失败，请稍后重试");
        }
    }

    /**
     * 分页查询传承人列表
     * @param current 当前页
     * @param size 每页大小
     * @param name 姓名关键词
     * @param title 称号
     * @param region 地区
     * @return 传承人分页列表
     */
    public Page<InheritorResponseDTO> getInheritorPage(Long current, Long size, String name, String title, String region) {
        try {
            Page<Inheritor> page = new Page<>(current, size);
            
            LambdaQueryWrapper<Inheritor> wrapper = new LambdaQueryWrapper<>();
            if (StringUtils.hasText(name)) {
                wrapper.like(Inheritor::getName, name);
            }
            if (StringUtils.hasText(title)) {
                wrapper.like(Inheritor::getTitle, title);
            }
            if (StringUtils.hasText(region)) {
                wrapper.like(Inheritor::getRegion, region);
            }
            wrapper.orderByDesc(Inheritor::getCreateTime);
            
            Page<Inheritor> inheritorPage = inheritorMapper.selectPage(page, wrapper);

            // 转换为响应DTO
            Page<InheritorResponseDTO> resultPage = new Page<>(
                    inheritorPage.getCurrent(), inheritorPage.getSize(), inheritorPage.getTotal());
            
            List<InheritorResponseDTO> records = inheritorPage.getRecords().stream()
                    .map(InheritorConvert::entityToResponse)
                    .collect(Collectors.toList());

            // 批量填充头像路径
            fillAvatarPathBatch(records);

            resultPage.setRecords(records);
            return resultPage;

        } catch (Exception e) {
            log.error("查询传承人列表失败", e);
            throw new ServiceException("查询失败，请稍后重试");
        }
    }

    /**
     * 更新传承人
     * @param inheritorId 传承人ID
     * @param updateDTO 更新命令
     * @return 更新后的传承人详情
     */
    @Transactional(rollbackFor = Exception.class)
    public InheritorDetailResponseDTO updateInheritor(String inheritorId, InheritorUpdateCommandDTO updateDTO) {
        try {
            Inheritor inheritor = inheritorMapper.selectById(inheritorId);
            if (inheritor == null) {
                throw new BusinessException("传承人不存在");
            }

            // 应用更新
            InheritorConvert.applyUpdateToEntity(inheritor, updateDTO);
            inheritorMapper.updateById(inheritor);

            log.info("传承人更新成功: id={}, name={}", inheritor.getId(), inheritor.getName());

            return getInheritorById(inheritorId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("传承人更新失败: inheritorId={}", inheritorId, e);
            throw new ServiceException("更新失败，请稍后重试");
        }
    }

    /**
     * 删除传承人
     * @param inheritorId 传承人ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteInheritor(String inheritorId) {
        try {
            Inheritor inheritor = inheritorMapper.selectById(inheritorId);
            if (inheritor == null) {
                throw new BusinessException("传承人不存在");
            }

            // 检查是否存在关联作品
            LambdaQueryWrapper<InheritorItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(InheritorItem::getInheritorId, inheritorId);
            Long count = inheritorItemMapper.selectCount(wrapper);
            
            if (count > 0) {
                throw new BusinessException("该传承人存在关联作品，请先解除关联");
            }

            // 删除传承人
            inheritorMapper.deleteById(inheritorId);

            log.info("传承人删除成功: id={}, name={}", inheritorId, inheritor.getName());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("传承人删除失败: inheritorId={}", inheritorId, e);
            throw new ServiceException("删除失败，请稍后重试");
        }
    }

    /**
     * 添加传承人与作品关联
     * @param inheritorId 传承人ID
     * @param itemId 作品ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void addItemRelation(String inheritorId, String itemId) {
        try {
            // 验证传承人是否存在
            Inheritor inheritor = inheritorMapper.selectById(inheritorId);
            if (inheritor == null) {
                throw new BusinessException("传承人不存在");
            }

            // 验证作品是否存在
            HeritageItem item = heritageItemMapper.selectById(itemId);
            if (item == null) {
                throw new BusinessException("作品不存在");
            }

            // 检查关联是否已存在
            LambdaQueryWrapper<InheritorItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(InheritorItem::getInheritorId, inheritorId)
                   .eq(InheritorItem::getItemId, itemId);
            InheritorItem existing = inheritorItemMapper.selectOne(wrapper);
            
            if (existing != null) {
                throw new BusinessException("关联已存在");
            }

            // 创建关联
            InheritorItem relation = InheritorItem.builder()
                    .inheritorId(inheritorId)
                    .itemId(itemId)
                    .build();
            inheritorItemMapper.insert(relation);

            log.info("传承人作品关联添加成功: inheritorId={}, itemId={}", inheritorId, itemId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("添加传承人作品关联失败: inheritorId={}, itemId={}", inheritorId, itemId, e);
            throw new ServiceException("添加关联失败，请稍后重试");
        }
    }

    /**
     * 移除传承人与作品关联
     * @param inheritorId 传承人ID
     * @param itemId 作品ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeItemRelation(String inheritorId, String itemId) {
        try {
            LambdaQueryWrapper<InheritorItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(InheritorItem::getInheritorId, inheritorId)
                   .eq(InheritorItem::getItemId, itemId);
            
            int deleted = inheritorItemMapper.delete(wrapper);
            if (deleted == 0) {
                throw new BusinessException("关联不存在");
            }

            log.info("传承人作品关联移除成功: inheritorId={}, itemId={}", inheritorId, itemId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("移除传承人作品关联失败: inheritorId={}, itemId={}", inheritorId, itemId, e);
            throw new ServiceException("移除关联失败，请稍后重试");
        }
    }

    /**
     * 查询传承人的关联作品列表
     * @param inheritorId 传承人ID
     * @return 关联作品列表
     */
    public List<HeritageItem> getRelatedItems(String inheritorId) {
        try {
            // 查询关联关系
            LambdaQueryWrapper<InheritorItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(InheritorItem::getInheritorId, inheritorId);
            List<InheritorItem> relations = inheritorItemMapper.selectList(wrapper);

            if (relations.isEmpty()) {
                return List.of();
            }

            // 查询作品详情
            List<String> itemIds = relations.stream()
                    .map(InheritorItem::getItemId)
                    .collect(Collectors.toList());

            LambdaQueryWrapper<HeritageItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.in(HeritageItem::getId, itemIds)
                       .orderByDesc(HeritageItem::getCreateTime);
            
            return heritageItemMapper.selectList(itemWrapper);

        } catch (Exception e) {
            log.error("查询传承人关联作品失败: inheritorId={}", inheritorId, e);
            throw new ServiceException("查询失败，请稍后重试");
        }
    }

    /**
     * 填充传承人关联信息
     */
    private void fillInheritorInfo(InheritorDetailResponseDTO response) {
        if (response == null) {
            return;
        }

        // 填充头像路径
        if (response.getAvatarFileId() != null) {
            SysFileInfo avatarFile = sysFileInfoMapper.selectById(response.getAvatarFileId());
            if (avatarFile != null) {
                InheritorConvert.fillAvatarPathForDetail(response, avatarFile.getFilePath());
            }
        }

        // 填充关联作品
        List<HeritageItem> relatedItems = getRelatedItems(response.getId());
        response.setHeritageItems(InheritorConvert.heritageItemListToInheritorItemResponseList(relatedItems));
    }

    /**
     * 批量填充头像路径
     */
    private void fillAvatarPathBatch(List<InheritorResponseDTO> responseList) {
        if (responseList == null || responseList.isEmpty()) {
            return;
        }

        // 获取所有头像文件ID
        List<Long> avatarFileIds = responseList.stream()
                .map(InheritorResponseDTO::getAvatarFileId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        if (avatarFileIds.isEmpty()) {
            return;
        }

        // 批量查询头像文件信息
        LambdaQueryWrapper<SysFileInfo> fileQuery = new LambdaQueryWrapper<>();
        fileQuery.in(SysFileInfo::getId, avatarFileIds);
        List<SysFileInfo> fileInfoList = sysFileInfoMapper.selectList(fileQuery);

        Map<Long, String> avatarPathMap = fileInfoList.stream()
                .collect(Collectors.toMap(
                        SysFileInfo::getId,
                        SysFileInfo::getFilePath,
                        (existing, replacement) -> existing
                ));

        // 填充头像路径
        InheritorConvert.fillAvatarPathBatch(responseList, avatarPathMap);
    }
}

