package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.response.DashboardStatisticsResponseDTO;
import org.example.springboot.entity.*;
import org.example.springboot.mapper.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 仪表板服务
 * @author system
 */
@Service
@Slf4j
public class DashboardService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ShopOrderMapper shopOrderMapper;

    @Resource
    private HeritageItemMapper heritageItemMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private ActivityMapper activityMapper;

    @Resource
    private ShopProductMapper shopProductMapper;

    @Resource
    private InheritorMapper inheritorMapper;

    /**
     * 获取仪表板统计数据
     */
    public DashboardStatisticsResponseDTO getStatistics() {
        log.info("开始获取仪表板统计数据");

        DashboardStatisticsResponseDTO statistics = DashboardStatisticsResponseDTO.builder()
                .totalUsers(getTotalUsers())
                .todayNewUsers(getTodayNewUsers())
                .totalOrders(getTotalOrders())
                .todayOrders(getTodayOrders())
                .totalSales(getTotalSales())
                .todaySales(getTodaySales())
                .totalHeritageItems(getTotalHeritageItems())
                .totalCourses(getTotalCourses())
                .totalActivities(getTotalActivities())
                .totalProducts(getTotalProducts())
                .totalInheritors(getTotalInheritors())
                .orderStatusDistribution(getOrderStatusDistribution())
                .heritageCategoryDistribution(getHeritageCategoryDistribution())
                .last7DaysOrders(getLast7DaysOrders())
                .last7DaysSales(getLast7DaysSales())
                .build();

        log.info("仪表板统计数据获取完成");
        return statistics;
    }

    /**
     * 获取总用户数
     */
    private Long getTotalUsers() {
        return userMapper.selectCount(null);
    }

    /**
     * 获取今日新增用户数
     */
    private Long getTodayNewUsers() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.ge(User::getCreatedAt, todayStart);
        return userMapper.selectCount(wrapper);
    }

    /**
     * 获取总订单数
     */
    private Long getTotalOrders() {
        return shopOrderMapper.selectCount(null);
    }

    /**
     * 获取今日订单数
     */
    private Long getTodayOrders() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<ShopOrder> wrapper = Wrappers.lambdaQuery();
        wrapper.ge(ShopOrder::getCreateTime, todayStart);
        return shopOrderMapper.selectCount(wrapper);
    }

    /**
     * 获取总销售额
     */
    private BigDecimal getTotalSales() {
        List<ShopOrder> orders = shopOrderMapper.selectList(
                Wrappers.lambdaQuery(ShopOrder.class)
                        .in(ShopOrder::getStatus, Arrays.asList(1, 2, 3)) // 已支付、已发货、已完成
        );
        return orders.stream()
                .map(ShopOrder::getPayAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 获取今日销售额
     */
    private BigDecimal getTodaySales() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        List<ShopOrder> orders = shopOrderMapper.selectList(
                Wrappers.lambdaQuery(ShopOrder.class)
                        .ge(ShopOrder::getCreateTime, todayStart)
                        .in(ShopOrder::getStatus, Arrays.asList(1, 2, 3))
        );
        return orders.stream()
                .map(ShopOrder::getPayAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 获取非遗项目总数
     */
    private Long getTotalHeritageItems() {
        return heritageItemMapper.selectCount(null);
    }

    /**
     * 获取课程总数
     */
    private Long getTotalCourses() {
        return courseMapper.selectCount(null);
    }

    /**
     * 获取活动总数
     */
    private Long getTotalActivities() {
        return activityMapper.selectCount(null);
    }

    /**
     * 获取商品总数
     */
    private Long getTotalProducts() {
        return shopProductMapper.selectCount(null);
    }

    /**
     * 获取传承人总数
     */
    private Long getTotalInheritors() {
        return inheritorMapper.selectCount(null);
    }

    /**
     * 获取订单状态分布
     */
    private Map<String, Long> getOrderStatusDistribution() {
        List<ShopOrder> orders = shopOrderMapper.selectList(null);
        Map<String, Long> distribution = new LinkedHashMap<>();
        distribution.put("待支付", orders.stream().filter(o -> o.getStatus() == 0).count());
        distribution.put("已支付", orders.stream().filter(o -> o.getStatus() == 1).count());
        distribution.put("已发货", orders.stream().filter(o -> o.getStatus() == 2).count());
        distribution.put("已完成", orders.stream().filter(o -> o.getStatus() == 3).count());
        distribution.put("已关闭", orders.stream().filter(o -> o.getStatus() == 4).count());
        return distribution;
    }

    /**
     * 获取非遗项目类别分布
     */
    private Map<String, Long> getHeritageCategoryDistribution() {
        List<HeritageItem> items = heritageItemMapper.selectList(null);
        return items.stream()
                .filter(item -> item.getCategory() != null && !item.getCategory().trim().isEmpty())
                .collect(Collectors.groupingBy(
                        HeritageItem::getCategory,
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
    }

    /**
     * 获取近7天订单趋势
     */
    private List<DashboardStatisticsResponseDTO.DailyStatistics> getLast7DaysOrders() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        List<DashboardStatisticsResponseDTO.DailyStatistics> result = new ArrayList<>();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            
            LambdaQueryWrapper<ShopOrder> wrapper = Wrappers.lambdaQuery();
            wrapper.ge(ShopOrder::getCreateTime, startOfDay)
                   .lt(ShopOrder::getCreateTime, endOfDay);
            
            Long count = shopOrderMapper.selectCount(wrapper);
            
            result.add(DashboardStatisticsResponseDTO.DailyStatistics.builder()
                    .date(date.format(formatter))
                    .count(count)
                    .build());
        }
        
        return result;
    }

    /**
     * 获取近7天销售趋势
     */
    private List<DashboardStatisticsResponseDTO.DailyStatistics> getLast7DaysSales() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        List<DashboardStatisticsResponseDTO.DailyStatistics> result = new ArrayList<>();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            
            List<ShopOrder> orders = shopOrderMapper.selectList(
                    Wrappers.lambdaQuery(ShopOrder.class)
                            .ge(ShopOrder::getCreateTime, startOfDay)
                            .lt(ShopOrder::getCreateTime, endOfDay)
                            .in(ShopOrder::getStatus, Arrays.asList(1, 2, 3))
            );
            
            BigDecimal amount = orders.stream()
                    .map(ShopOrder::getPayAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            result.add(DashboardStatisticsResponseDTO.DailyStatistics.builder()
                    .date(date.format(formatter))
                    .amount(amount)
                    .build());
        }
        
        return result;
    }

}

