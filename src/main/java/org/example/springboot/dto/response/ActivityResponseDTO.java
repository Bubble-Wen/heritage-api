package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动列表响应DTO
 * @author system
 */
@Data
@Schema(description = "活动列表响应")
public class ActivityResponseDTO {

    @Schema(description = "活动ID")
    private String id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "活动类型")
    private String type;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "地点")
    private String location;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "封面文件ID")
    private Long coverFileId;

    @Schema(description = "封面文件路径")
    private String coverFilePath;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}


