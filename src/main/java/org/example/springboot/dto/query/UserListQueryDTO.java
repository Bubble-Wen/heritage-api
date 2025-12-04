package org.example.springboot.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户列表查询DTO
 * @author system
 */
@Data
@Schema(description = "用户列表查询")
public class UserListQueryDTO {

    @Schema(description = "用户名（模糊查询）", example = "admin")
    private String username;

    @Schema(description = "邮箱（模糊查询）", example = "admin@drone.com")
    private String email;



    @Schema(description = "姓名（模糊查询）", example = "张三")
    private String name;

    @Schema(description = "用户类型", example = "ADMIN", allowableValues = {"USER", "ADMIN"})
    private String userType;

    @Schema(description = "用户状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    @Schema(description = "当前页码", example = "1")
    private Integer currentPage = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
}
