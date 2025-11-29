package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.*;
import org.example.springboot.dto.query.UserListQueryDTO;
import org.example.springboot.dto.response.UserDetailResponseDTO;
import org.example.springboot.dto.response.UserLoginResponseDTO;
import org.example.springboot.common.Result;
import org.example.springboot.enums.UserType;
import org.example.springboot.service.UserService;
import org.example.springboot.util.JwtTokenUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 * @author system
 */
@Tag(name = "用户管理")
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginCommandDTO loginDTO) {
        log.info("用户登录请求: {}", loginDTO.getUsername());
        UserLoginResponseDTO response = userService.login(loginDTO);
        return Result.success("登录成功", response);
    }

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/add")
    public Result<UserDetailResponseDTO> register(@Valid @RequestBody UserRegisterCommandDTO registerDTO) {
        log.info("用户注册请求: {}", registerDTO.getUsername());
        UserDetailResponseDTO response = userService.register(registerDTO);
        return Result.success("注册成功", response);
    }




    /**
     * 忘记密码 - 通过用户名、邮箱、手机号三要素验证后重置密码
     */
    @Operation(summary = "忘记密码")
    @PostMapping("/forget")
    public Result<Void> forgetPassword(@Valid @RequestBody UserResetPasswordCommandDTO resetDTO) {
        log.info("忘记密码请求: username={}", resetDTO.getUsername());
        userService.resetPasswordByVerification(resetDTO);
        return Result.success();
    }




    /**
     * 获取当前登录用户信息
     */
    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/current")
    public Result<UserDetailResponseDTO> getCurrentUser() {
        try {
            UserDetailResponseDTO currentUser = JwtTokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("未登录或登录已过期");
            }
            log.info("获取当前用户信息: {}", currentUser.getUsername());
            return Result.success(currentUser);
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
            return Result.error("获取用户信息失败");
        }
    }

    /**
     * 更新用户信息
     */
    @Operation(summary = "更新用户信息")
    @PutMapping("/{id}")
    public Result<UserDetailResponseDTO> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody UserUpdateCommandDTO updateDTO) {
        
        try {
            UserDetailResponseDTO currentUser = JwtTokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("未登录或登录已过期");
            }
            
            // 只能更新自己的信息，除非是管理员
            if (!currentUser.getId().equals(id) && !currentUser.getUserType().equals("ADMIN")) {
                return Result.error("无权限修改其他用户信息");
            }
            
            log.info("更新用户信息: userId={}", id);
            UserDetailResponseDTO response = userService.updateUser(id, updateDTO);
            return Result.success("更新成功", response);
        } catch (Exception e) {
            log.error("更新用户信息失败: userId={}", id, e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 修改用户密码
     */
    @Operation(summary = "修改用户密码")
    @PutMapping("/password/{id}")
    public Result<Void> updatePassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody UserPasswordUpdateCommandDTO passwordDTO) {
        
        try {
            UserDetailResponseDTO currentUser = JwtTokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("未登录或登录已过期");
            }
            
            // 只能修改自己的密码
            if (!currentUser.getId().equals(id)) {
                return Result.error("无权限修改其他用户密码");
            }
            
            log.info("修改用户密码: userId={}", id);
            userService.updatePassword(id, passwordDTO);
            return Result.success();
        } catch (Exception e) {
            log.error("修改用户密码失败: userId={}", id, e);
            return Result.error("密码修改失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询用户列表（管理员功能）
     */
    @Operation(summary = "分页查询用户列表")
    @GetMapping("/page")
    public Result<Page<UserDetailResponseDTO>> getUserPage(
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "邮箱") @RequestParam(required = false) String email,
            @Parameter(description = "姓名") @RequestParam(required = false) String name,
            @Parameter(description = "用户类型") @RequestParam(required = false) String userType,
            @Parameter(description = "用户状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size
          ) {

        // 权限检查：只有管理员可以查看用户列表
        UserDetailResponseDTO currentUser = JwtTokenUtils.getCurrentUser();
        if (currentUser == null || !UserType.ADMIN.getCode().equals(currentUser.getUserType())) {
            return Result.error("权限不足");
        }

        UserListQueryDTO queryDTO = new UserListQueryDTO();
        queryDTO.setUsername(username);
        queryDTO.setEmail(email);
        queryDTO.setName(name);
        queryDTO.setUserType(userType);
        queryDTO.setStatus(status);
        queryDTO.setCurrentPage(currentPage);
        queryDTO.setSize(size);

        log.info("管理员查询用户列表: page={}, size={}", currentPage, size);
        Page<UserDetailResponseDTO> response = userService.getUserPage(queryDTO);
        return Result.success(response);
    }




}
