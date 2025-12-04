package org.example.springboot.service.convert;

import org.example.springboot.dto.command.UserRegisterCommandDTO;
import org.example.springboot.dto.command.UserUpdateCommandDTO;
import org.example.springboot.dto.response.UserDetailResponseDTO;
import org.example.springboot.dto.response.UserLoginResponseDTO;
import org.example.springboot.entity.User;
import org.example.springboot.enums.UserStatus;

import java.time.LocalDateTime;

/**
 * 用户转换类
 * @author system
 */
public class UserConvert {

    /**
     * 注册命令DTO转换为User实体
     * @param registerDTO 注册命令DTO
     * @param encodedPassword 加密后的密码
     * @return User实体
     */
    public static User registerCommandToEntity(UserRegisterCommandDTO registerDTO, String encodedPassword) {
        return User.builder()
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .password(encodedPassword)
                .name(registerDTO.getName())
                .phone(registerDTO.getPhone())
                .sex(registerDTO.getSex())
                .userType(registerDTO.getUserType())
                .status(UserStatus.NORMAL.getCode()) // 默认正常状态
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * User实体转换为详情响应DTO
     * @param user User实体
     * @return 用户详情响应DTO
     */
    public static UserDetailResponseDTO entityToDetailResponse(User user) {
        return UserDetailResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .sex(user.getSex())
                .userType(user.getUserType())
                .userTypeDisplayName(user.getUserTypeDisplayName())
                .status(user.getStatus())
                .statusDisplayName(user.getStatusDisplayName())
                .displayName(user.getDisplayName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * 构建登录响应DTO
     * @param token JWT令牌
     * @param userInfo 用户信息
     * @return 登录响应DTO
     */
    public static UserLoginResponseDTO buildLoginResponse(String token, UserDetailResponseDTO userInfo) {
        return UserLoginResponseDTO.builder()
                .userInfo(userInfo)
                .token(token)
                .roleType(userInfo.getUserType())
                .build();
    }

    /**
     * 更新命令DTO应用到User实体
     * @param user 原User实体
     * @param updateDTO 更新命令DTO
     */
    public static void applyUpdateToEntity(User user, UserUpdateCommandDTO updateDTO) {
        if (updateDTO.getEmail() != null) {
            user.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getName() != null) {
            user.setName(updateDTO.getName());
        }
        if (updateDTO.getAvatar() != null) {
            user.setAvatar(updateDTO.getAvatar());
        }
        if (updateDTO.getPhone() != null) {
            user.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getSex() != null) {
            user.setSex(updateDTO.getSex());
        }
        if (updateDTO.getUserType() != null) {
            user.setUserType(updateDTO.getUserType());
        }
        if (updateDTO.getStatus() != null) {
            user.setStatus(updateDTO.getStatus());
        }
        user.setUpdatedAt(LocalDateTime.now());
    }
}
