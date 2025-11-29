package org.example.springboot.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.response.UserDetailResponseDTO;
import org.example.springboot.enums.UserStatus;
import org.example.springboot.service.UserService;
import org.example.springboot.util.JwtTokenUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT认证过滤器
 * 1. 继承OncePerRequestFilter确保每个请求只执行一次
 * 2. 集成Spring Security认证机制
 * 3. 统一的token验证和用户上下文设置
 * 4. 完善的异常处理和日志记录
 * 5. 标准的用户认证系统，支持角色权限
 *
 * @author system
 * @date 2025-01-13
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        log.debug("JWT认证过滤器处理请求：{} {}", method, requestUri);

        try {
            // 1. 提取JWT token
            String token = extractToken(request);

            if (StringUtils.hasText(token)) {
                log.debug("成功提取token，长度：{}，前20字符：{}",
                        token.length(),
                        token.length() > 20 ? token.substring(0, 20) + "..." : token);

                // 2. 验证token并获取用户ID
                Long userId = getUserIdFromToken(token);

                if (userId != null) {
                    // 检查token是否过期
                    if (JwtTokenUtils.isTokenExpired(token)) {
                        log.warn("JWT token已过期，用户ID：{}", userId);
                        SecurityContextHolder.clearContext();
                        filterChain.doFilter(request, response);
                        return;
                    }

                    // 3. 查询用户信息（验证用户是否仍然存在和有效）
                    UserDetailResponseDTO user = userService.getUserById(userId);

                    if (user != null && user.getStatus().equals(UserStatus.NORMAL.getCode())) {
                        // 4. 创建Spring Security认证对象
                        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_" + user.getUserType())
                        );

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        user.getUsername(),
                                        null,
                                        authorities
                                );

                        // 5. 设置认证信息到Spring Security上下文
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // 6. 设置用户信息到请求属性（方便Controller使用）
                        request.setAttribute("currentUser", user);
                        request.setAttribute("currentUserId", userId);

                        log.debug("JWT认证成功，用户ID：{}，用户名：{}，用户类型：{}", 
                                userId, user.getUsername(), user.getUserType());
                    } else {
                        log.warn("JWT验证失败：用户不存在或已被禁用，用户ID：{}", userId);
                        // 清理认证上下文，防止安全问题
                        SecurityContextHolder.clearContext();
                    }
                } else {
                    log.warn("JWT验证失败：无法从token中解析用户ID");
                    // 清理认证上下文
                    SecurityContextHolder.clearContext();
                }
            } else {
                log.debug("未找到token，跳过JWT认证");
            }
        } catch (JWTVerificationException e) {
            log.warn("JWT验证失败：{}，清理认证上下文", e.getMessage());
            // JWT验证失败时清理认证上下文
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            log.error("JWT认证过程中发生异常，请求：{} {}，异常：{}，清理认证上下文", method, requestUri, e.getMessage(), e);
            // 发生异常时清理认证上下文，确保安全
            SecurityContextHolder.clearContext();
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中提取JWT token
     * 只支持标准的 Authorization: Bearer <token> 方式
     *
     * @param request HTTP请求对象
     * @return JWT token字符串，如果没有找到则返回null
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (org.springframework.util.StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * 从token中获取用户ID
     * @param token JWT token
     * @return 用户ID
     */
    private Long getUserIdFromToken(String token) {
        try {
            return JwtTokenUtils.verifyToken(token).getClaim("userId").asLong();
        } catch (Exception e) {
            log.error("从token获取用户ID失败", e);
            return null;
        }
    }
} 