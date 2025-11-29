package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 仪表板统计数据响应DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "仪表板统计数据响应")
public class DashboardStatisticsResponseDTO {

    @Schema(description = "总用户数")
    private Long totalUsers;

    @Schema(description = "今日新增用户数")
    private Long todayNewUsers;

    @Schema(description = "总订单数")
    private Long totalOrders;

    @Schema(description = "今日订单数")
    private Long todayOrders;

    @Schema(description = "总销售额")
    private BigDecimal totalSales;

    @Schema(description = "今日销售额")
    private BigDecimal todaySales;

    @Schema(description = "非遗项目总数")
    private Long totalHeritageItems;

    @Schema(description = "课程总数")
    private Long totalCourses;

    @Schema(description = "活动总数")
    private Long totalActivities;

    @Schema(description = "商品总数")
    private Long totalProducts;

    @Schema(description = "传承人总数")
    private Long totalInheritors;

    @Schema(description = "订单状态分布")
    private Map<String, Long> orderStatusDistribution;

    @Schema(description = "非遗项目类别分布")
    private Map<String, Long> heritageCategoryDistribution;

    @Schema(description = "近7天订单趋势")
    private List<DailyStatistics> last7DaysOrders;

    @Schema(description = "近7天销售趋势")
    private List<DailyStatistics> last7DaysSales;

    /**
     * 每日统计数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "每日统计数据")
    public static class DailyStatistics {
        @Schema(description = "日期")
        private String date;

        @Schema(description = "数值")
        private Long count;

        @Schema(description = "金额")
        private BigDecimal amount;
    }
}

