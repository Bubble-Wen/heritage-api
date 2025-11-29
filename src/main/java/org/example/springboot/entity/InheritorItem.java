package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 传承人与作品关联实体类
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("inheritor_item")
@Schema(description = "传承人与作品关联实体类")
public class InheritorItem {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "传承人ID")
    @NotBlank(message = "传承人ID不能为空")
    @Size(max = 50, message = "传承人ID长度不能超过50个字符")
    private String inheritorId;

    @Schema(description = "作品ID")
    @NotBlank(message = "作品ID不能为空")
    @Size(max = 50, message = "作品ID长度不能超过50个字符")
    private String itemId;
}

