package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.common.Result;
import org.example.springboot.dto.response.DashboardStatisticsResponseDTO;
import org.example.springboot.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仪表板控制器
 * @author system
 */
@Tag(name = "仪表板管理", description = "仪表板数据统计接口")
@RestController
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    /**
     * 获取仪表板统计数据
     */
    @Operation(summary = "获取仪表板统计数据", description = "获取系统各项数据统计信息")
    @GetMapping("/statistics")
    public Result<DashboardStatisticsResponseDTO> getStatistics() {
        log.info("获取仪表板统计数据");
        DashboardStatisticsResponseDTO statistics = dashboardService.getStatistics();
        return Result.success(statistics);
    }
}

