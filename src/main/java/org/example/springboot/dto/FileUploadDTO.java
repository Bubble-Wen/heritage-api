package org.example.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 文件上传请求DTO
 * @author system
 */
@Data
@Schema(description = "文件上传请求DTO")
public class FileUploadDTO {

    @NotBlank(message = "业务类型不能为空")
    @Schema(description = "业务类型", example = "USER_AVATAR")
    private String businessType;

    @NotNull(message = "业务对象ID不能为空")
    @Schema(description = "业务对象ID", example = "1")
    private String businessId;

    @Schema(description = "业务字段名", example = "avatar")
    private String businessField;

    @Schema(description = "是否临时文件", example = "false")
    private Boolean isTemp;
} 