package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品图片响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "商品图片响应")
public class ShopProductImageResponseDTO {

    @Schema(description = "文件ID")
    private Long id;

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "文件类型")
    private String type;

    @Schema(description = "文件大小(字节)")
    private Long size;

    @Schema(description = "排序号")
    private Integer sortOrder;
}

