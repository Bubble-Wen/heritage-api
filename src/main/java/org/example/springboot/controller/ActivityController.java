package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.example.springboot.common.Result;
import org.example.springboot.dto.command.ActivityCreateCommandDTO;
import org.example.springboot.dto.command.ActivityUpdateCommandDTO;
import org.example.springboot.dto.command.ActivitySignupCreateCommandDTO;
import org.example.springboot.dto.response.ActivityDetailResponseDTO;
import org.example.springboot.dto.response.ActivityResponseDTO;
import org.example.springboot.dto.response.ActivitySignupResponseDTO;
import org.example.springboot.service.ActivityService;
import org.example.springboot.service.ActivitySignupService;

import java.util.List;

/**
 * 活动管理控制器
 * @author system
 */
@Tag(name = "活动管理", description = "活动的增删改查、报名管理等操作")
@RequestMapping("/activity")
@RestController
@Slf4j
@Validated
public class ActivityController {

    @Resource
    private ActivityService activityService;

    @Resource
    private ActivitySignupService activitySignupService;

    /**
     * 创建活动
     */
    @Operation(summary = "创建活动", description = "创建新的活动")
    @PostMapping("/create")
    public Result<ActivityDetailResponseDTO> createActivity(
            @Valid @RequestBody ActivityCreateCommandDTO createDTO) {
        log.info("创建活动请求: title={}, type={}", createDTO.getTitle(), createDTO.getType());
        ActivityDetailResponseDTO response = activityService.createActivity(createDTO);
        return Result.success(response);
    }

    /**
     * 根据ID获取活动详情
     */
    @Operation(summary = "获取活动详情", description = "根据活动ID获取详细信息")
    @GetMapping("/{activityId}")
    public Result<ActivityDetailResponseDTO> getActivityById(
            @Parameter(description = "活动ID") @PathVariable String activityId) {
        log.info("获取活动详情请求: activityId={}", activityId);
        ActivityDetailResponseDTO response = activityService.getActivityById(activityId);
        return Result.success(response);
    }

    /**
     * 分页查询活动列表
     */
    @Operation(summary = "分页查询活动列表", description = "根据条件分页查询活动列表")
    @GetMapping("/page")
    public Result<Page<ActivityResponseDTO>> getActivityPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "标题关键词") @RequestParam(required = false) String title,
            @Parameter(description = "活动类型") @RequestParam(required = false) String type,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        log.info("分页查询活动列表: page={}, size={}, title={}", current, size, title);

        Page<ActivityResponseDTO> response = activityService.getActivityPage(current, size, title, type, status);
        return Result.success(response);
    }

    /**
     * 更新活动
     */
    @Operation(summary = "更新活动", description = "更新活动信息")
    @PutMapping("/{activityId}")
    public Result<ActivityDetailResponseDTO> updateActivity(
            @Parameter(description = "活动ID") @PathVariable String activityId,
            @Valid @RequestBody ActivityUpdateCommandDTO updateDTO) {
        log.info("更新活动请求: activityId={}", activityId);
        ActivityDetailResponseDTO response = activityService.updateActivity(activityId, updateDTO);
        return Result.success(response);
    }

    /**
     * 删除活动
     */
    @Operation(summary = "删除活动", description = "删除活动")
    @DeleteMapping("/{activityId}")
    public Result<Void> deleteActivity(
            @Parameter(description = "活动ID") @PathVariable String activityId) {
        log.info("删除活动请求: activityId={}", activityId);
        activityService.deleteActivity(activityId);
        return Result.success();
    }

    /**
     * 获取最新活动列表
     */
    @Operation(summary = "获取最新活动列表", description = "获取最新发布的活动列表")
    @GetMapping("/latest")
    public Result<List<ActivityResponseDTO>> getLatestActivities(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取最新活动列表请求: limit={}", limit);
        List<ActivityResponseDTO> response = activityService.getLatestActivities(limit);
        return Result.success(response);
    }

    /**
     * 报名活动
     */
    @Operation(summary = "报名活动", description = "用户报名参加活动")
    @PostMapping("/signup")
    public Result<ActivitySignupResponseDTO> signupActivity(
            @Valid @RequestBody ActivitySignupCreateCommandDTO createDTO) {
        log.info("活动报名请求: activityId={}, userId={}", createDTO.getActivityId(), createDTO.getUserId());
        ActivitySignupResponseDTO response = activitySignupService.createSignup(createDTO);
        return Result.success(response);
    }

    /**
     * 获取活动的报名列表
     */
    @Operation(summary = "获取活动报名列表", description = "查询活动的报名记录")
    @GetMapping("/{activityId}/signups")
    public Result<Page<ActivitySignupResponseDTO>> getActivitySignups(
            @Parameter(description = "活动ID") @PathVariable String activityId,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        log.info("获取活动报名列表: activityId={}, page={}, size={}", activityId, current, size);
        Page<ActivitySignupResponseDTO> response = activitySignupService.getSignupsByActivityId(activityId, current, size, status);
        return Result.success(response);
    }

    /**
     * 审核通过报名
     */
    @Operation(summary = "审核通过报名", description = "管理员审核通过活动报名")
    @PutMapping("/signup/{signupId}/approve")
    public Result<ActivitySignupResponseDTO> approveSignup(
            @Parameter(description = "报名ID") @PathVariable Long signupId) {
        log.info("审核通过报名请求: signupId={}", signupId);
        ActivitySignupResponseDTO response = activitySignupService.approveSignup(signupId);
        return Result.success(response);
    }

    /**
     * 审核拒绝报名
     */
    @Operation(summary = "审核拒绝报名", description = "管理员审核拒绝活动报名")
    @PutMapping("/signup/{signupId}/reject")
    public Result<ActivitySignupResponseDTO> rejectSignup(
            @Parameter(description = "报名ID") @PathVariable Long signupId) {
        log.info("审核拒绝报名请求: signupId={}", signupId);
        ActivitySignupResponseDTO response = activitySignupService.rejectSignup(signupId);
        return Result.success(response);
    }

    /**
     * 签到
     */
    @Operation(summary = "活动签到", description = "用户参加活动时签到")
    @PutMapping("/signup/{signupId}/checkin")
    public Result<ActivitySignupResponseDTO> checkIn(
            @Parameter(description = "报名ID") @PathVariable Long signupId) {
        log.info("活动签到请求: signupId={}", signupId);
        ActivitySignupResponseDTO response = activitySignupService.checkIn(signupId);
        return Result.success(response);
    }

    /**
     * 获取报名详情
     */
    @Operation(summary = "获取报名详情", description = "查询报名记录详情")
    @GetMapping("/signup/{signupId}")
    public Result<ActivitySignupResponseDTO> getSignupById(
            @Parameter(description = "报名ID") @PathVariable Long signupId) {
        log.info("获取报名详情请求: signupId={}", signupId);
        ActivitySignupResponseDTO response = activitySignupService.getSignupById(signupId);
        return Result.success(response);
    }
}

