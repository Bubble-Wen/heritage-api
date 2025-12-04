//package org.example.springboot.ai;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import jakarta.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
//import org.example.springboot.entity.HeritageItem;
//import org.example.springboot.entity.Inheritor;
//import org.example.springboot.entity.Activity;
//import org.example.springboot.entity.Course;
//import org.example.springboot.entity.ShopProduct;
//import org.example.springboot.mapper.HeritageItemMapper;
//import org.example.springboot.mapper.InheritorMapper;
//import org.example.springboot.mapper.ActivityMapper;
//import org.example.springboot.mapper.CourseMapper;
//import org.example.springboot.mapper.ShopProductMapper;
//import org.springframework.ai.tool.annotation.Tool;
//import org.springframework.ai.tool.annotation.ToolParam;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
///**
// * 非遗智能助手工具函数
// *
// * 职责：定义AI可以调用的外部工具，用于查询非遗相关数据
// *
// * @author system
// */
//@Slf4j
//@Component
//public class HeritageTools {
//
//    @Resource
//    private HeritageItemMapper heritageItemMapper;
//
//    @Resource
//    private InheritorMapper inheritorMapper;
//
//    @Resource
//    private ActivityMapper activityMapper;
//
//    @Resource
//    private CourseMapper courseMapper;
//
//    @Resource
//    private ShopProductMapper shopProductMapper;
//
//    // ==================== 非遗项目查询工具 ====================
//
//    /**
//     * 搜索非遗项目
//     *
//     * @param keyword 搜索关键词
//     * @return 非遗项目列表
//     */
//    @Tool(
//        name = "searchHeritageItems",
//        description = "搜索非遗项目（如苏绣、蜀绣、皮影戏、景德镇瓷器等），支持按名称模糊搜索。" +
//                "当用户询问'有哪些非遗项目'、'XX非遗'、'查找XX'等问题时使用此工具。",
//        returnDirect = false
//    )
//    public String searchHeritageItems(
//        @ToolParam(description = "搜索关键词，如'刺绣'、'陶瓷'、'皮影'等") String keyword
//    ) {
//        try {
//            log.info("AI工具调用: 搜索非遗项目, keyword={}", keyword);
//
//            LambdaQueryWrapper<HeritageItem> wrapper = new LambdaQueryWrapper<>();
//            wrapper.like(HeritageItem::getTitle, keyword)
//                   .or().like(HeritageItem::getDescription, keyword)
//                   .eq(HeritageItem::getStatus, 1)  // 只查询已审核的
//                   .orderByDesc(HeritageItem::getCreateTime)
//                   .last("LIMIT 10");  // 限制返回数量
//
//            List<HeritageItem> items = heritageItemMapper.selectList(wrapper);
//
//            if (items.isEmpty()) {
//                return String.format("未找到包含'%s'的非遗项目，您可以尝试其他关键词。", keyword);
//            }
//
//            StringBuilder result = new StringBuilder(String.format("找到 %d 个相关非遗项目：\n\n", items.size()));
//            for (HeritageItem item : items) {
//                result.append(String.format("**%s**\n", item.getTitle()));
//                result.append(String.format("- 类别：%s\n", item.getCategory()));
//                result.append(String.format("- 地域：%s\n", item.getRegion()));
//                if (item.getDescription() != null && !item.getDescription().isEmpty()) {
//                    String desc = item.getDescription().length() > 100
//                        ? item.getDescription().substring(0, 100) + "..."
//                        : item.getDescription();
//                    result.append(String.format("- 简介：%s\n", desc));
//                }
//                result.append("\n");
//            }
//
//            return result.toString();
//
//        } catch (Exception e) {
//            log.error("搜索非遗项目失败: {}", e.getMessage(), e);
//            return "查询非遗项目时发生错误，请稍后重试。";
//        }
//    }
//
//    /**
//     * 按类别查询非遗项目
//     *
//     * @param category 非遗类别关键词
//     * @return 非遗项目列表
//     */
//    @Tool(
//        name = "getHeritageItemsByCategory",
//        description = "按类别查询非遗项目。支持按类别关键词搜索，如'手工艺'、'表演艺术'、'民俗'、'刺绣'、'陶瓷'等。" +
//                "当用户询问'有哪些手工艺'、'刺绣类非遗'等问题时使用此工具。",
//        returnDirect = false
//    )
//    public String getHeritageItemsByCategory(
//        @ToolParam(description = "类别关键词，如'手工艺'、'刺绣'、'陶瓷'等") String category
//    ) {
//        try {
//            log.info("AI工具调用: 按类别查询非遗项目, category={}", category);
//
//            LambdaQueryWrapper<HeritageItem> wrapper = new LambdaQueryWrapper<>();
//            wrapper.like(HeritageItem::getCategory, category)
//                   .eq(HeritageItem::getStatus, 2)  // 只查询已发布的
//                   .orderByDesc(HeritageItem::getCreateTime)
//                   .last("LIMIT 10");
//
//            List<HeritageItem> items = heritageItemMapper.selectList(wrapper);
//
//            if (items.isEmpty()) {
//                return String.format("暂无'%s'类别的非遗项目。", category);
//            }
//
//            StringBuilder result = new StringBuilder(String.format("**%s**类别的非遗项目：\n\n", category));
//            for (HeritageItem item : items) {
//                result.append(String.format("- **%s**（%s）\n", item.getTitle(), item.getRegion()));
//            }
//
//            return result.toString();
//
//        } catch (Exception e) {
//            log.error("按类别查询非遗项目失败: {}", e.getMessage(), e);
//            return "查询非遗项目时发生错误，请稍后重试。";
//        }
//    }
//
//    // ==================== 传承人查询工具 ====================
//
//    /**
//     * 搜索非遗传承人
//     *
//     * @param keyword 搜索关键词
//     * @return 传承人列表
//     */
//    @Tool(
//        name = "searchInheritors",
//        description = "搜索非遗传承人信息，支持按姓名、称号、地域搜索。" +
//                "当用户询问'XX传承人'、'有哪些大师'、'传承人信息'等问题时使用此工具。",
//        returnDirect = false
//    )
//    public String searchInheritors(
//        @ToolParam(description = "搜索关键词，如传承人姓名、称号或地域") String keyword
//    ) {
//        try {
//            log.info("AI工具调用: 搜索传承人, keyword={}", keyword);
//
//            LambdaQueryWrapper<Inheritor> wrapper = new LambdaQueryWrapper<>();
//            wrapper.like(Inheritor::getName, keyword)
//                   .or().like(Inheritor::getTitle, keyword)
//                   .or().like(Inheritor::getRegion, keyword)
//                   .orderByDesc(Inheritor::getCreateTime)
//                   .last("LIMIT 10");
//
//            List<Inheritor> inheritors = inheritorMapper.selectList(wrapper);
//
//            if (inheritors.isEmpty()) {
//                return String.format("未找到包含'%s'的传承人信息。", keyword);
//            }
//
//            StringBuilder result = new StringBuilder(String.format("找到 %d 位传承人：\n\n", inheritors.size()));
//            for (Inheritor inheritor : inheritors) {
//                result.append(String.format("**%s**\n", inheritor.getName()));
//                if (inheritor.getTitle() != null && !inheritor.getTitle().isEmpty()) {
//                    result.append(String.format("- 称号：%s\n", inheritor.getTitle()));
//                }
//                if (inheritor.getRegion() != null && !inheritor.getRegion().isEmpty()) {
//                    result.append(String.format("- 地域：%s\n", inheritor.getRegion()));
//                }
//                if (inheritor.getBio() != null && !inheritor.getBio().isEmpty()) {
//                    String bio = inheritor.getBio().length() > 80
//                        ? inheritor.getBio().substring(0, 80) + "..."
//                        : inheritor.getBio();
//                    result.append(String.format("- 简介：%s\n", bio));
//                }
//                result.append("\n");
//            }
//
//            return result.toString();
//
//        } catch (Exception e) {
//            log.error("搜索传承人失败: {}", e.getMessage(), e);
//            return "查询传承人信息时发生错误，请稍后重试。";
//        }
//    }
//
//    // ==================== 活动查询工具 ====================
//
//    /**
//     * 查询正在进行的活动
//     *
//     * @return 活动列表
//     */
//    @Tool(
//        name = "getOngoingActivities",
//        description = "查询当前正在进行或即将开始的非遗活动。" +
//                "当用户询问'有什么活动'、'最近的活动'、'可以参加的活动'等问题时使用此工具。",
//        returnDirect = false
//    )
//    public String getOngoingActivities() {
//        try {
//            log.info("AI工具调用: 查询正在进行的活动");
//
//            LocalDateTime now = LocalDateTime.now();
//            LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
//            wrapper.eq(Activity::getStatus, 1)
//                   .ge(Activity::getEndTime, now)  // 结束时间大于等于现在
//                   .orderByAsc(Activity::getStartTime)
//                   .last("LIMIT 10");
//
//            List<Activity> activities = activityMapper.selectList(wrapper);
//
//            if (activities.isEmpty()) {
//                return "当前暂无正在进行或即将开始的活动，请关注后续更新。";
//            }
//
//            StringBuilder result = new StringBuilder("**正在进行或即将开始的活动：**\n\n");
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//
//            for (Activity activity : activities) {
//                result.append(String.format("**%s**\n", activity.getTitle()));
//                result.append(String.format("- 时间：%s 至 %s\n",
//                    activity.getStartTime().format(formatter),
//                    activity.getEndTime().format(formatter)));
//                result.append(String.format("- 地点：%s\n", activity.getLocation()));
//
//                // 判断活动状态
//                if (activity.getStartTime().isAfter(now)) {
//                    result.append("- 状态：即将开始\n");
//                } else {
//                    result.append("- 状态：进行中\n");
//                }
//
//                if (activity.getDescription() != null) {
//                    String desc = activity.getDescription().length() > 80
//                        ? activity.getDescription().substring(0, 80) + "..."
//                        : activity.getDescription();
//                    result.append(String.format("- 简介：%s\n", desc));
//                }
//                result.append("\n");
//            }
//
//            return result.toString();
//
//        } catch (Exception e) {
//            log.error("查询活动失败: {}", e.getMessage(), e);
//            return "查询活动信息时发生错误，请稍后重试。";
//        }
//    }
//
//    // ==================== 课程查询工具 ====================
//
//    /**
//     * 搜索课程
//     *
//     * @param keyword 搜索关键词
//     * @return 课程列表
//     */
//    @Tool(
//        name = "searchCourses",
//        description = "搜索非遗课程，支持按课程名称、技艺类型搜索。" +
//                "当用户询问'有什么课程'、'学习XX'、'课程推荐'等问题时使用此工具。",
//        returnDirect = false
//    )
//    public String searchCourses(
//        @ToolParam(description = "搜索关键词，如课程名称或技艺类型") String keyword
//    ) {
//        try {
//            log.info("AI工具调用: 搜索课程, keyword={}", keyword);
//
//            LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
//            wrapper.like(Course::getTitle, keyword)
//                   .or().like(Course::getDescription, keyword)
//                   .eq(Course::getStatus, 1)
//                   .orderByDesc(Course::getCreateTime)
//                   .last("LIMIT 10");
//
//            List<Course> courses = courseMapper.selectList(wrapper);
//
//            if (courses.isEmpty()) {
//                return String.format("未找到包含'%s'的课程。", keyword);
//            }
//
//            StringBuilder result = new StringBuilder(String.format("找到 %d 个相关课程：\n\n", courses.size()));
//            for (Course course : courses) {
//                result.append(String.format("**%s**\n", course.getTitle()));
//                if (course.getLevel() != null && !course.getLevel().isEmpty()) {
//                    result.append(String.format("- 难度：%s\n", course.getLevel()));
//                }
//                if (course.getDescription() != null) {
//                    String desc = course.getDescription().length() > 80
//                        ? course.getDescription().substring(0, 80) + "..."
//                        : course.getDescription();
//                    result.append(String.format("- 简介：%s\n", desc));
//                }
//                result.append("\n");
//            }
//
//            return result.toString();
//
//        } catch (Exception e) {
//            log.error("搜索课程失败: {}", e.getMessage(), e);
//            return "查询课程信息时发生错误，请稍后重试。";
//        }
//    }
//
//    // ==================== 商品查询工具 ====================
//
//    /**
//     * 搜索非遗手办商品
//     *
//     * @param keyword 搜索关键词
//     * @return 商品列表
//     */
//    @Tool(
//        name = "searchProducts",
//        description = "搜索非遗手办商品，支持按商品名称搜索。" +
//                "当用户询问'有什么商品'、'想买XX'、'手办推荐'等问题时使用此工具。",
//        returnDirect = false
//    )
//    public String searchProducts(
//        @ToolParam(description = "搜索关键词，如商品名称或类型") String keyword
//    ) {
//        try {
//            log.info("AI工具调用: 搜索商品, keyword={}", keyword);
//
//            LambdaQueryWrapper<ShopProduct> wrapper = new LambdaQueryWrapper<>();
//            wrapper.like(ShopProduct::getTitle, keyword)
//                   .or().like(ShopProduct::getDetail, keyword)
//                   .eq(ShopProduct::getStatus, 1)
//                   .orderByDesc(ShopProduct::getCreateTime)
//                   .last("LIMIT 10");
//
//            List<ShopProduct> products = shopProductMapper.selectList(wrapper);
//
//            if (products.isEmpty()) {
//                return String.format("未找到包含'%s'的商品。", keyword);
//            }
//
//            StringBuilder result = new StringBuilder(String.format("找到 %d 个相关商品：\n\n", products.size()));
//            for (ShopProduct product : products) {
//                result.append(String.format("**%s**\n", product.getTitle()));
//                result.append(String.format("- 价格：¥%.2f\n", product.getPrice()));
//                if (product.getStock() != null) {
//                    result.append(String.format("- 库存：%d\n", product.getStock()));
//                }
//                if (product.getDetail() != null) {
//                    String detail = product.getDetail().length() > 80
//                        ? product.getDetail().substring(0, 80) + "..."
//                        : product.getDetail();
//                    result.append(String.format("- 详情：%s\n", detail));
//                }
//                result.append("\n");
//            }
//
//            return result.toString();
//
//        } catch (Exception e) {
//            log.error("搜索商品失败: {}", e.getMessage(), e);
//            return "查询商品信息时发生错误，请稍后重试。";
//        }
//    }
//
//}
//
