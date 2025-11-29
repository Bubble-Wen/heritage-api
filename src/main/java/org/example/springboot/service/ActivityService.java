package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.example.springboot.entity.Activity;
import org.example.springboot.entity.ActivitySignup;
import org.example.springboot.entity.SysFileInfo;
import org.example.springboot.mapper.ActivityMapper;
import org.example.springboot.mapper.ActivitySignupMapper;
import org.example.springboot.mapper.SysFileInfoMapper;
import org.example.springboot.dto.command.ActivityCreateCommandDTO;
import org.example.springboot.dto.command.ActivityUpdateCommandDTO;
import org.example.springboot.dto.response.ActivityDetailResponseDTO;
import org.example.springboot.dto.response.ActivityResponseDTO;
import org.example.springboot.enums.ActivityStatus;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.service.convert.ActivityConvert;

/**
 * 活动业务逻辑层
 * @author system
 */
@Slf4j
@Service
public class ActivityService {

    @Resource
    private ActivityMapper activityMapper;

    @Resource
    private ActivitySignupMapper activitySignupMapper;

    @Resource
    private SysFileInfoMapper sysFileInfoMapper;

    /**
     * 创建活动
     * @param createDTO 创建命令
     * @return 活动详情
     */
    @Transactional(rollbackFor = Exception.class)
    public ActivityDetailResponseDTO createActivity(ActivityCreateCommandDTO createDTO) {
        try {
            // 验证UUID是否提供（策略B要求）
            if (!StringUtils.hasText(createDTO.getId())) {
                throw new BusinessException("活动ID不能为空，请使用UUID预生成策略");
            }

            // 验证UUID是否已存在
            Activity existingActivity = activityMapper.selectById(createDTO.getId());
            if (existingActivity != null) {
                throw new BusinessException("活动ID已存在，请重新生成UUID");
            }

            // 创建活动实体
            Activity activity = ActivityConvert.createCommandToEntity(createDTO);
            activityMapper.insert(activity);

            log.info("活动创建成功: id={}, title={}", activity.getId(), activity.getTitle());

            // 查询并返回详情
            return getActivityById(activity.getId());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("活动创建失败", e);
            throw new ServiceException("活动创建失败，请稍后重试");
        }
    }

    /**
     * 根据ID获取活动详情
     * @param activityId 活动ID
     * @return 活动详情
     */
    public ActivityDetailResponseDTO getActivityById(String activityId) {
        try {
            Activity activity = activityMapper.selectById(activityId);
            if (activity == null) {
                throw new BusinessException("活动不存在");
            }

            ActivityDetailResponseDTO response = ActivityConvert.entityToDetailResponse(activity);

            // 查询封面文件路径（只查询未删除的文件）
            if (activity.getCoverFileId() != null) {
                SysFileInfo coverFile = sysFileInfoMapper.selectById(activity.getCoverFileId());
                if (coverFile != null && coverFile.getStatus() != null && coverFile.getStatus() == 1) {
                    response.setCoverFilePath(coverFile.getFilePath());
                }
            }

            // 查询报名人数
            LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ActivitySignup::getActivityId, activityId); // 直接使用字符串ID
            Long signupCount = activitySignupMapper.selectCount(wrapper);
            response.setSignupCount(signupCount);

            return response;

        } catch (BusinessException e) {
            throw e;
        } catch (NumberFormatException e) {
            throw new BusinessException("活动ID格式错误");
        } catch (Exception e) {
            log.error("获取活动详情失败: activityId={}", activityId, e);
            throw new ServiceException("获取活动详情失败，请稍后重试");
        }
    }

    /**
     * 更新活动
     * @param activityId 活动ID
     * @param updateDTO 更新命令
     * @return 活动详情
     */
    @Transactional(rollbackFor = Exception.class)
    public ActivityDetailResponseDTO updateActivity(String activityId, ActivityUpdateCommandDTO updateDTO) {
        try {
            Activity activity = activityMapper.selectById(activityId);
            if (activity == null) {
                throw new BusinessException("活动不存在");
            }

            // 验证状态转换是否合法
            if (updateDTO.getStatus() != null && !updateDTO.getStatus().equals(activity.getStatus())) {
                if (!ActivityStatus.isValidTransition(activity.getStatus(), updateDTO.getStatus())) {
                    throw new BusinessException("状态转换不合法");
                }
            }

            // 应用更新
            ActivityConvert.applyUpdateToEntity(activity, updateDTO);
            activityMapper.updateById(activity);

            log.info("活动更新成功: id={}", activityId);

            return getActivityById(activityId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("活动更新失败: activityId={}", activityId, e);
            throw new ServiceException("活动更新失败，请稍后重试");
        }
    }

    /**
     * 分页查询活动列表
     * @param current 当前页
     * @param size 每页大小
     * @param title 标题（模糊查询）
     * @param type 活动类型
     * @param status 状态
     * @return 分页结果
     */
    public Page<ActivityResponseDTO> getActivityPage(Long current, Long size, String title, String type, Integer status) {
        try {
            Page<Activity> page = new Page<>(current, size);
            LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();

            if (StrUtil.isNotBlank(title)) {
                wrapper.like(Activity::getTitle, title);
            }
            if (StrUtil.isNotBlank(type)) {
                wrapper.eq(Activity::getType, type);
            }
            if (status != null) {
                wrapper.eq(Activity::getStatus, status);
            }

            // 按创建时间倒序
            wrapper.orderByDesc(Activity::getCreateTime);

            Page<Activity> activityPage = activityMapper.selectPage(page, wrapper);

            // 转换为响应DTO
            List<ActivityResponseDTO> responseDTOList = activityPage.getRecords().stream()
                    .map(activity -> {
                        ActivityResponseDTO dto = ActivityConvert.entityToResponse(activity);
                        // 查询封面文件路径（只查询未删除的文件）
                        if (activity.getCoverFileId() != null) {
                            SysFileInfo coverFile = sysFileInfoMapper.selectById(activity.getCoverFileId());
                            if (coverFile != null && coverFile.getStatus() != null && coverFile.getStatus() == 1) {
                                dto.setCoverFilePath(coverFile.getFilePath());
                            }
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());

            Page<ActivityResponseDTO> responsePage = new Page<>(activityPage.getCurrent(), activityPage.getSize(), activityPage.getTotal());
            responsePage.setRecords(responseDTOList);

            return responsePage;

        } catch (Exception e) {
            log.error("分页查询活动列表失败", e);
            throw new ServiceException("分页查询活动列表失败，请稍后重试");
        }
    }

    /**
     * 删除活动
     * @param activityId 活动ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteActivity(String activityId) {
        try {
            Activity activity = activityMapper.selectById(activityId);
            if (activity == null) {
                throw new BusinessException("活动不存在");
            }

            // 检查是否有报名记录
            checkRelatedSignups(activityId);

            // 删除活动
            activityMapper.deleteById(activityId);

            log.info("活动删除成功: id={}", activityId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("活动删除失败: activityId={}", activityId, e);
            throw new ServiceException("活动删除失败，请稍后重试");
        }
    }

    /**
     * 检查关联的报名记录
     * @param activityId 活动ID
     */
    private void checkRelatedSignups(String activityId) {
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignup::getActivityId, activityId);
        Long count = activitySignupMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该活动存在报名记录，无法删除");
        }
    }

    /**
     * 获取最新活动列表
     * @param limit 限制数量
     * @return 活动列表
     */
    public List<ActivityResponseDTO> getLatestActivities(Integer limit) {
        try {
            LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Activity::getStatus, ActivityStatus.REGISTERING.getCode())
                    .orderByDesc(Activity::getCreateTime)
                    .last("LIMIT " + limit);

            List<Activity> activities = activityMapper.selectList(wrapper);

            return activities.stream()
                    .map(activity -> {
                        ActivityResponseDTO dto = ActivityConvert.entityToResponse(activity);
                        if (activity.getCoverFileId() != null) {
                            SysFileInfo coverFile = sysFileInfoMapper.selectById(activity.getCoverFileId());
                            if (coverFile != null && coverFile.getStatus() != null && coverFile.getStatus() == 1) {
                                dto.setCoverFilePath(coverFile.getFilePath());
                            }
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("获取最新活动列表失败", e);
            throw new ServiceException("获取最新活动列表失败，请稍后重试");
        }
    }
}

