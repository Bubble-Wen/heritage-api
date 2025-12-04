package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import org.example.springboot.entity.User;
import org.example.springboot.mapper.UserMapper;
import org.example.springboot.dto.command.*;
import org.example.springboot.dto.query.*;
import org.example.springboot.dto.response.*;
import org.example.springboot.enums.UserType;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.util.JwtTokenUtils;
import org.example.springboot.service.convert.UserConvert;

/**
 * 用户业务逻辑层
 * @author ftfx
 */
@Slf4j
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户登录
     * @param loginDTO 登录命令
     * @return 登录响应
     */
    public UserLoginResponseDTO login(UserLoginCommandDTO loginDTO) {
        try {
            // 根据用户名或邮箱查找用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, loginDTO.getUsername())
                    .or()
                    .eq(User::getEmail, loginDTO.getUsername());
            User user = userMapper.selectOne(queryWrapper);


            if (user == null) {
                throw new BusinessException("用户不存在");
            }

            // 验证密码
            if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                throw new BusinessException("用户名或密码错误");
            }

            // 检查用户状态
            if (!user.isActive()) {
                throw new BusinessException("账号已被禁用，请联系管理员");
            }

            // 生成JWT token
            String token = JwtTokenUtils.generateToken(user.getId(), user.getUsername(), user.getUserType());

            // 构建响应
            UserDetailResponseDTO userInfo = UserConvert.entityToDetailResponse(user);
            return UserConvert.buildLoginResponse(token, userInfo);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("用户登录失败", e);
            throw new ServiceException("登录失败，请稍后重试");
        }
    }

    /**
     * 用户注册
     * @param registerDTO 注册命令
     * @return 用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public UserDetailResponseDTO register(UserRegisterCommandDTO registerDTO) {
        try {
            // 验证密码确认
            if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
                throw new BusinessException("两次输入的密码不一致");
            }

            // 检查用户名是否存在
            LambdaQueryWrapper<User> usernameQuery = new LambdaQueryWrapper<>();
            usernameQuery.eq(User::getUsername, registerDTO.getUsername());
            if (userMapper.selectCount(usernameQuery) > 0) {
                throw new BusinessException("用户名已存在");
            }

            // 检查邮箱是否存在
            LambdaQueryWrapper<User> emailQuery = new LambdaQueryWrapper<>();
            emailQuery.eq(User::getEmail, registerDTO.getEmail());
            if (userMapper.selectCount(emailQuery) > 0) {
                throw new BusinessException("邮箱已被注册");
            }

            // 验证用户类型
            if (!UserType.isValidCode(registerDTO.getUserType())) {
                throw new BusinessException("无效的用户类型");
            }

            // 创建用户
            String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
            User user = UserConvert.registerCommandToEntity(registerDTO, encodedPassword);

            userMapper.insert(user);
            log.info("用户注册成功: {}", user.getUsername());

            return UserConvert.entityToDetailResponse(user);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("用户注册失败", e);
            throw new ServiceException("注册失败，请稍后重试");
        }
    }

    /**
     * 根据ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    public UserDetailResponseDTO getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return UserConvert.entityToDetailResponse(user);
    }



    /**
     * 分页查询用户列表
     * @param queryDTO 查询条件
     * @return 用户分页列表
     */
    public Page<UserDetailResponseDTO> getUserPage(UserListQueryDTO queryDTO) {
        try {
            Page<User> page = new Page<>(queryDTO.getCurrentPage(), queryDTO.getSize());

            // 构建查询条件
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.hasText(queryDTO.getUsername())) {
                queryWrapper.like(User::getUsername, queryDTO.getUsername());
            }
            if (StringUtils.hasText(queryDTO.getEmail())) {
                queryWrapper.like(User::getEmail, queryDTO.getEmail());
            }

            if (StringUtils.hasText(queryDTO.getName())) {
                queryWrapper.like(User::getName, queryDTO.getName());
            }
            if (StringUtils.hasText(queryDTO.getUserType())) {
                queryWrapper.eq(User::getUserType, queryDTO.getUserType());
            }
            if (queryDTO.getStatus() != null) {
                queryWrapper.eq(User::getStatus, queryDTO.getStatus());
            }
            queryWrapper.orderByDesc(User::getCreatedAt);

            Page<User> userPage = userMapper.selectPage(page, queryWrapper);

            // 转换为响应DTO
            Page<UserDetailResponseDTO> resultPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
            List<UserDetailResponseDTO> records = userPage.getRecords().stream()
                    .map(UserConvert::entityToDetailResponse)
                    .toList();
            resultPage.setRecords(records);

            return resultPage;

        } catch (Exception e) {
            log.error("查询用户列表失败", e);
            throw new ServiceException("查询失败，请稍后重试");
        }
    }

    /**
     * 删除用户
     * @param userId 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }

            // 检查是否为管理员（防止删除管理员）
            if (user.isAdmin()) {
                throw new BusinessException("不能删除管理员账号");
            }

            userMapper.deleteById(userId);
            log.info("用户删除成功: {}", user.getUsername());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("用户删除失败", e);
            throw new ServiceException("删除失败，请稍后重试");
        }
    }





    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param updateDTO 更新信息
     * @return 更新后的用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public UserDetailResponseDTO updateUser(Long userId, UserUpdateCommandDTO updateDTO) {
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }

            // 检查邮箱是否已被其他用户使用
            if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
                LambdaQueryWrapper<User> emailQuery = new LambdaQueryWrapper<>();
                emailQuery.eq(User::getEmail, updateDTO.getEmail())
                        .ne(User::getId, userId);
                if (userMapper.selectCount(emailQuery) > 0) {
                    throw new BusinessException("邮箱已被其他用户使用");
                }
            }

            // 验证用户类型和状态的有效性
            if (updateDTO.getUserType() != null && !UserType.isValidCode(updateDTO.getUserType())) {
                throw new BusinessException("无效的用户类型");
            }

            // 应用更新
            UserConvert.applyUpdateToEntity(user, updateDTO);
            userMapper.updateById(user);

            log.info("用户信息更新成功: {}", user.getUsername());
            return UserConvert.entityToDetailResponse(user);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("用户信息更新失败", e);
            throw new ServiceException("更新失败，请稍后重试");
        }
    }

    /**
     * 修改用户密码
     * @param userId 用户ID
     * @param passwordDTO 密码更新信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long userId, UserPasswordUpdateCommandDTO passwordDTO) {
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }

            // 验证旧密码
            if (!passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())) {
                throw new BusinessException("旧密码不正确");
            }

            // 更新密码
            user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);

            log.info("用户密码修改成功: {}", user.getUsername());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("密码修改失败", e);
            throw new ServiceException("密码修改失败，请稍后重试");
        }
    }

    /**
     * 通过邮箱重置密码
     * @param email 邮箱
     * @param newPassword 新密码
     */
    @Transactional(rollbackFor = Exception.class)
    public void resetPasswordByEmail(String email, String newPassword) {
        try {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getEmail, email);
            User user = userMapper.selectOne(queryWrapper);

            if (user == null) {
                throw new BusinessException("邮箱不存在");
            }

            // 重置密码
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);

            log.info("用户密码重置成功: {}", user.getUsername());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("密码重置失败", e);
            throw new ServiceException("密码重置失败，请稍后重试");
        }
    }

    /**
     * 通过用户名、邮箱、手机号三要素验证后重置密码
     * @param resetDTO 重置密码命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void resetPasswordByVerification(UserResetPasswordCommandDTO resetDTO) {
        try {
            // 根据用户名查询用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, resetDTO.getUsername());
            User user = userMapper.selectOne(queryWrapper);

            if (user == null) {
                throw new BusinessException("用户名不存在");
            }

            // 验证邮箱是否匹配
            if (!resetDTO.getEmail().equals(user.getEmail())) {
                throw new BusinessException("邮箱与用户名不匹配");
            }

            // 验证手机号是否匹配
            if (!resetDTO.getPhone().equals(user.getPhone())) {
                throw new BusinessException("手机号与用户名不匹配");
            }

            // 三要素验证通过，重置密码
            user.setPassword(passwordEncoder.encode(resetDTO.getNewPassword()));
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);

            log.info("用户密码重置成功（三要素验证）: {}", user.getUsername());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("密码重置失败", e);
            throw new ServiceException("密码重置失败，请稍后重试");
        }
    }


}
