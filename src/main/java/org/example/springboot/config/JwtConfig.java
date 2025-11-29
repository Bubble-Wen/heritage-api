package org.example.springboot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT配置类 - 管理JWT相关的配置属性
 * 
 * 配置项：
 * - jwt.secret: JWT签名密钥
 * - jwt.expiration: JWT过期时间（毫秒）
 * - jwt.refresh-expiration: JWT刷新token过期时间（毫秒）
 * 
 * 使用方式：
 * 在application.properties中配置相应的属性值
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    
    /**
     * JWT签名密钥
     * 默认值：应在配置文件中设置复杂的密钥
     */
    private String secret = "defaultSecretKey";
    
    /**
     * JWT过期时间（毫秒）
     * 默认值：24小时 = 24 * 60 * 60 * 1000 = 86400000
     */
    private Long expiration = 86400000L;
    
    /**
     * JWT刷新token过期时间（毫秒）
     * 默认值：7天 = 7 * 24 * 60 * 60 * 1000 = 604800000
     */
    private Long refreshExpiration = 604800000L;
    
    /**
     * token头部名称
     * 默认值：Authorization
     */
    private String header = "Authorization";
    
    /**
     * token前缀
     * 默认值：Bearer 
     */
    private String tokenPrefix = "Bearer ";
    
    // Getter and Setter methods
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    public Long getExpiration() {
        return expiration;
    }
    
    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }
    
    public Long getRefreshExpiration() {
        return refreshExpiration;
    }
    
    public void setRefreshExpiration(Long refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }
    
    public String getHeader() {
        return header;
    }
    
    public void setHeader(String header) {
        this.header = header;
    }
    
    public String getTokenPrefix() {
        return tokenPrefix;
    }
    
    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }
    
    @Override
    public String toString() {
        return "JwtConfig{" +
                "secret='***'" + // 不输出真实密钥
                ", expiration=" + expiration +
                ", refreshExpiration=" + refreshExpiration +
                ", header='" + header + '\'' +
                ", tokenPrefix='" + tokenPrefix + '\'' +
                '}';
    }
} 