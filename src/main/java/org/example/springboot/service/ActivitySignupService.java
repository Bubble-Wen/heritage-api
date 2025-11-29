package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import org.example.springboot.entity.Activity;
import org.example.springboot.entity.ActivitySignup;
import org.example.springboot.entity.User;
import org.example.springboot.mapper.ActivityMapper;
import org.example.springboot.mapper.ActivitySignupMapper;
import org.example.springboot.mapper.UserMapper;
import org.example.springboot.dto.command.ActivitySignupCreateCommandDTO;
import org.example.springboot.dto.response.ActivitySignupResponseDTO;
import org.example.springboot.enums.ActivitySignupStatus;
import org.example.springboot.enums.ActivityStatus;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.service.convert.ActivitySignupConvert;

/**
 * 活动报名业务逻辑层
 * @author system
 */
@Slf4j
@Service
public class ActivitySignupService {

    @Resource
    private ActivitySignupMapper activitySignupMapper;

    @Resource
    private ActivityMapper activityMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 创建报名
     * @param createDTO 创建命令
     * @return 报名详情
     */
    @Transactional(rollbackFor = Exception.class)
    public ActivitySignupResponseDTO createSignup(ActivitySignupCreateCommandDTO createDTO) {
        try {
            // 验证活动是否存在
            Activity activity = activityMapper.selectById(createDTO.getActivityId());
            if (activity == null) {
                throw new BusinessException("活动不存在");
            }

            // 验证活动状态是否可以报名
            if (!activity.canSignup()) {
                String activityStatusName = activity.getStatusDisplayName();
                if (activity.isDraft()) {
                    throw new BusinessException("活动还在筹备中(状态: " + activityStatusName + ")，暂时不接受报名");
                } else if (activity.isInProgress()) {
                    throw new BusinessException("活动已经开始(状态: " + activityStatusName + ")，报名时间已过");
                } else if (activity.isFinished()) {
                    throw new BusinessException("活动已经结束(状态: " + activityStatusName + ")，无法报名");
                } else {
                    throw new BusinessException("活动当前状态(" + activityStatusName + ")不接受报名，只有报名中的活动才能报名");
                }
            }

            // 验证用户是否存在
            User user = userMapper.selectById(createDTO.getUserId());
            if (user == null) {
                throw new BusinessException("用户不存在");
            }

            // 检查是否已经报名
            LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ActivitySignup::getActivityId, createDTO.getActivityId())
                   .eq(ActivitySignup::getUserId, createDTO.getUserId());
            ActivitySignup existingSignup = activitySignupMapper.selectOne(wrapper);
            if (existingSignup != null) {
                throw new BusinessException("您已经报名该活动");
            }

            // 创建报名记录
            ActivitySignup signup = ActivitySignupConvert.createCommandToEntity(createDTO);
            activitySignupMapper.insert(signup);

            log.info("活动报名成功: activityId={}, userId={}", createDTO.getActivityId(), createDTO.getUserId());

            // 查询并返回详情
            return getSignupById(signup.getId());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("活动报名失败", e);
            throw new ServiceException("活动报名失败，请稍后重试");
        }
    }

    /**
     * 根据ID获取报名详情
     * @param signupId 报名ID
     * @return 报名详情
     */
    public ActivitySignupResponseDTO getSignupById(Long signupId) {
        try {
            ActivitySignup signup = activitySignupMapper.selectById(signupId);
            if (signup == null) {
                throw new BusinessException("报名记录不存在");
            }

            ActivitySignupResponseDTO response = ActivitySignupConvert.entityToResponse(signup);

            // 查询活动标题
            Activity activity = activityMapper.selectById(signup.getActivityId());
            if (activity != null) {
                response.setActivityTitle(activity.getTitle());
            }

            // 查询用户名
            User user = userMapper.selectById(signup.getUserId());
            if (user != null) {
                response.setUsername(user.getUsername());
            }

            return response;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取报名详情失败: signupId={}", signupId, e);
            throw new ServiceException("获取报名详情失败，请稍后重试");
        }
    }

    /**
     * 获取活动的报名列表
     * @param activityId 活动ID
     * @param current 当前页
     * @param size 每页大小
     * @param status 状态
     * @return 分页结果
     */
    public Page<ActivitySignupResponseDTO> getSignupsByActivityId(String activityId, Long current, Long size, Integer status) {
        try {
            Page<ActivitySignup> page = new Page<>(current, size);
            LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ActivitySignup::getActivityId, activityId);
            if (status != null) {
                wrapper.eq(ActivitySignup::getStatus, status);
            }
            wrapper.orderByDesc(ActivitySignup::getCreateTime);

            Page<ActivitySignup> signupPage = activitySignupMapper.selectPage(page, wrapper);

            // 转换为响应DTO
            List<ActivitySignupResponseDTO> responseDTOList = signupPage.getRecords().stream()
                    .map(signup -> {
                        ActivitySignupResponseDTO dto = ActivitySignupConvert.entityToResponse(signup);
                        // 查询活动标题
                        Activity activity = activityMapper.selectById(signup.getActivityId());
                        if (activity != null) {
                            dto.setActivityTitle(activity.getTitle());
                        }
                        // 查询用户名
                        User user = userMapper.selectById(signup.getUserId());
                        if (user != null) {
                            dto.setUsername(user.getUsername());
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());

            Page<ActivitySignupResponseDTO> responsePage = new Page<>(signupPage.getCurrent(), signupPage.getSize(), signupPage.getTotal());
            responsePage.setRecords(responseDTOList);

            return responsePage;

        } catch (Exception e) {
            log.error("获取活动报名列表失败: activityId={}", activityId, e);
            throw new ServiceException("获取活动报名列表失败，请稍后重试");
        }
    }

    /**
     * 审核通过
     * @param signupId 报名ID
     * @return 报名详情
     */
    @Transactional(rollbackFor = Exception.class)
    public ActivitySignupResponseDTO approveSignup(Long signupId) {
        try {
            ActivitySignup signup = activitySignupMapper.selectById(signupId);
            if (signup == null) {
                throw new BusinessException("报名记录不存在");
            }

            // 验证状态转换是否合法
            if (!ActivitySignupStatus.isValidTransition(signup.getStatus(), ActivitySignupStatus.APPROVED.getCode())) {
                throw new BusinessException("当前状态无法审核通过");
            }

            signup.setStatus(ActivitySignupStatus.APPROVED.getCode());
            activitySignupMapper.updateById(signup);

            log.info("报名审核通过: signupId={}", signupId);

            return getSignupById(signupId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("报名审核通过失败: signupId={}", signupId, e);
            throw new ServiceException("报名审核通过失败，请稍后重试");
        }
    }

    /**
     * 审核拒绝
     * @param signupId 报名ID
     * @return 报名详情
     */
    @Transactional(rollbackFor = Exception.class)
    public ActivitySignupResponseDTO rejectSignup(Long signupId) {
        try {
            ActivitySignup signup = activitySignupMapper.selectById(signupId);
            if (signup == null) {
                throw new BusinessException("报名记录不存在");
            }

            // 验证状态转换是否合法
            if (!ActivitySignupStatus.isValidTransition(signup.getStatus(), ActivitySignupStatus.REJECTED.getCode())) {
                throw new BusinessException("当前状态无法审核拒绝");
            }

            signup.setStatus(ActivitySignupStatus.REJECTED.getCode());
            activitySignupMapper.updateById(signup);

            log.info("报名审核拒绝: signupId={}", signupId);

            return getSignupById(signupId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("报名审核拒绝失败: signupId={}", signupId, e);
            throw new ServiceException("报名审核拒绝失败，请稍后重试");
        }
    }

    /**
     * 签到
     * @param signupId 报名ID
     * @return 报名详情
     */
    @Transactional(rollbackFor = Exception.class)
    public ActivitySignupResponseDTO checkIn(Long signupId) {
        try {
            ActivitySignup signup = activitySignupMapper.selectById(signupId);
            if (signup == null) {
                throw new BusinessException("报名记录不存在");
            }

            // 验证报名状态是否可以签到
            if (!signup.canCheckIn()) {
                String currentStatusName = signup.getStatusDisplayName();
                if (signup.isCheckedIn()) {
                    throw new BusinessException("该报名已经签到过了，无需重复签到");
                } else if (signup.isPending()) {
                    throw new BusinessException("报名还在审核中，请等待审核通过后再签到");
                } else if (signup.isRejected()) {
                    throw new BusinessException("报名已被拒绝，无法签到");
                } else {
                    throw new BusinessException("当前报名状态(" + currentStatusName + ")无法签到，只有审核通过的报名才能签到");
                }
            }

            // 验证活动状态是否可以签到
            Activity activity = activityMapper.selectById(signup.getActivityId());
            if (activity == null) {
                throw new BusinessException("关联的活动不存在");
            }
            
            if (!activity.canCheckIn()) {
                String activityStatusName = activity.getStatusDisplayName();
                if (activity.isDraft()) {
                    throw new BusinessException("活动还在筹备中(状态: " + activityStatusName + ")，暂时无法签到");
                } else if (activity.isSigningUp()) {
                    throw new BusinessException("活动还在报名阶段(状态: " + activityStatusName + ")，请等待活动开始后再签到");
                } else if (activity.isFinished()) {
                    throw new BusinessException("活动已经结束(状态: " + activityStatusName + ")，签到时间已过");
                } else {
                    throw new BusinessException("活动当前状态(" + activityStatusName + ")不允许签到，只有进行中的活动才能签到");
                }
            }

            signup.setStatus(ActivitySignupStatus.CHECKED_IN.getCode());
            activitySignupMapper.updateById(signup);

            log.info("报名签到成功: signupId={}, activityId={}, activityStatus={}", 
                    signupId, activity.getId(), activity.getStatusDisplayName());

            return getSignupById(signupId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("报名签到失败: signupId={}", signupId, e);
            throw new ServiceException("报名签到失败，请稍后重试");
        }
    }
}

