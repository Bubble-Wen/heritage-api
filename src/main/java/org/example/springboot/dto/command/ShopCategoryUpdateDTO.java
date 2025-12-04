package org.example.springboot.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 商品分类更新DTO
 * @author system
 */
@Data
@Schema(description = "商品分类更新DTO")
public class ShopCategoryUpdateDTO {

    @Schema(description = "分类ID")
    @NotNull(message = "分类ID不能为空")
    private Long id;

    @Schema(description = "分类名称")
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100个字符")
    private String name;

    @Schema(description = "状态 0禁用 1启用")
    private Integer status;
}
