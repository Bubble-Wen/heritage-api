package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.entity.ShopOrder;
import org.example.springboot.entity.ShopOrderItem;
import org.example.springboot.entity.ShopProduct;
import org.example.springboot.mapper.ShopOrderItemMapper;
import org.example.springboot.mapper.ShopOrderMapper;
import org.example.springboot.mapper.ShopProductMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品推荐服务 - 基于物品的协同过滤算法
 * @author system
 */
@Slf4j
@Service
public class ProductRecommendService {

    @Resource
    private ShopOrderMapper shopOrderMapper;

    @Resource
    private ShopOrderItemMapper shopOrderItemMapper;

    @Resource
    private ShopProductMapper shopProductMapper;

    /**
     * 获取商品推荐列表（基于物品的协同过滤）
     * @param productId 当前商品ID
     * @param limit 推荐数量
     * @return 推荐商品ID列表
     */
    public List<String> getRecommendedProducts(String productId, int limit) {
        log.info("开始计算商品推荐，商品ID: {}, 推荐数量: {}", productId, limit);

        try {
            // 1. 构建用户-商品购买矩阵
            Map<Long, Set<String>> userProductMatrix = buildUserProductMatrix();
            
            if (userProductMatrix.isEmpty()) {
                log.warn("用户购买数据为空，使用fallback推荐策略");
                return getFallbackRecommendations(productId, limit);
            }

            // 2. 计算商品相似度
            Map<String, Double> similarityScores = calculateItemSimilarity(productId, userProductMatrix);
            
            if (similarityScores.isEmpty()) {
                log.warn("未找到相似商品，使用fallback推荐策略");
                return getFallbackRecommendations(productId, limit);
            }

            // 3. 按相似度排序并返回Top N
            List<String> recommendedProductIds = similarityScores.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .filter(id -> isProductAvailable(id)) // 过滤掉不可用的商品
                    .collect(Collectors.toList());

            // 4. 如果推荐数量不足，使用fallback策略补充
            if (recommendedProductIds.size() < limit) {
                log.info("推荐数量不足，使用fallback策略补充");
                List<String> fallbackIds = getFallbackRecommendations(productId, limit - recommendedProductIds.size());
                for (String fallbackId : fallbackIds) {
                    if (!recommendedProductIds.contains(fallbackId)) {
                        recommendedProductIds.add(fallbackId);
                        if (recommendedProductIds.size() >= limit) {
                            break;
                        }
                    }
                }
            }

            log.info("商品推荐计算完成，推荐数量: {}", recommendedProductIds.size());
            return recommendedProductIds;
            
        } catch (Exception e) {
            log.error("计算商品推荐失败", e);
            return getFallbackRecommendations(productId, limit);
        }
    }

    /**
     * 构建用户-商品购买矩阵
     * 返回 Map<用户ID, Set<商品ID>>
     */
    private Map<Long, Set<String>> buildUserProductMatrix() {
        // 查询所有已支付的订单
        LambdaQueryWrapper<ShopOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.in(ShopOrder::getStatus, Arrays.asList(1, 2, 3)); // 已支付、已发货、已完成
        List<ShopOrder> orders = shopOrderMapper.selectList(orderWrapper);

        if (orders.isEmpty()) {
            return Collections.emptyMap();
        }

        // 获取所有订单的商品明细
        List<Long> orderIds = orders.stream().map(ShopOrder::getId).collect(Collectors.toList());
        LambdaQueryWrapper<ShopOrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.in(ShopOrderItem::getOrderId, orderIds);
        List<ShopOrderItem> orderItems = shopOrderItemMapper.selectList(itemWrapper);

        // 构建用户-商品矩阵
        Map<Long, Set<String>> matrix = new HashMap<>();
        for (ShopOrder order : orders) {
            Long userId = order.getUserId();
            matrix.putIfAbsent(userId, new HashSet<>());
            
            // 找到该订单的所有商品
            for (ShopOrderItem item : orderItems) {
                if (item.getOrderId().equals(order.getId())) {
                    matrix.get(userId).add(item.getProductId());
                }
            }
        }

        log.info("构建用户-商品矩阵完成，用户数: {}", matrix.size());
        return matrix;
    }

    /**
     * 计算商品相似度（基于余弦相似度）
     * @param targetProductId 目标商品ID
     * @param userProductMatrix 用户-商品购买矩阵
     * @return Map<商品ID, 相似度分数>
     */
    private Map<String, Double> calculateItemSimilarity(String targetProductId, Map<Long, Set<String>> userProductMatrix) {
        Map<String, Double> similarityScores = new HashMap<>();

        // 找出购买过目标商品的用户集合
        Set<Long> targetProductUsers = userProductMatrix.entrySet().stream()
                .filter(entry -> entry.getValue().contains(targetProductId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        if (targetProductUsers.isEmpty()) {
            log.warn("没有用户购买过目标商品: {}", targetProductId);
            return similarityScores;
        }

        // 统计所有候选商品及其被购买的用户集合
        Map<String, Set<Long>> candidateProductUsers = new HashMap<>();
        for (Long userId : targetProductUsers) {
            Set<String> userProducts = userProductMatrix.get(userId);
            for (String productId : userProducts) {
                if (!productId.equals(targetProductId)) {
                    candidateProductUsers.putIfAbsent(productId, new HashSet<>());
                    candidateProductUsers.get(productId).add(userId);
                }
            }
        }

        // 计算余弦相似度
        for (Map.Entry<String, Set<Long>> entry : candidateProductUsers.entrySet()) {
            String candidateProductId = entry.getKey();
            Set<Long> candidateUsers = entry.getValue();

            // 计算交集（同时购买两个商品的用户数）
            Set<Long> intersection = new HashSet<>(targetProductUsers);
            intersection.retainAll(candidateUsers);
            int commonUsers = intersection.size();

            // 余弦相似度公式: cos(θ) = (A·B) / (|A| × |B|)
            // A·B = 同时购买两个商品的用户数
            // |A| = 购买商品A的用户数
            // |B| = 购买商品B的用户数
            double similarity = commonUsers / (Math.sqrt(targetProductUsers.size()) * Math.sqrt(candidateUsers.size()));
            
            similarityScores.put(candidateProductId, similarity);
        }

        log.info("计算商品相似度完成，候选商品数: {}", similarityScores.size());
        return similarityScores;
    }

    /**
     * 检查商品是否可用（上架且有库存）
     */
    private boolean isProductAvailable(String productId) {
        ShopProduct product = shopProductMapper.selectById(productId);
        return product != null && product.getStatus() == 1 && product.getStock() > 0;
    }

    /**
     * Fallback推荐策略：推荐同类目的热门商品
     * @param productId 当前商品ID
     * @param limit 推荐数量
     * @return 推荐商品ID列表
     */
    private List<String> getFallbackRecommendations(String productId, int limit) {
        ShopProduct currentProduct = shopProductMapper.selectById(productId);
        
        LambdaQueryWrapper<ShopProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShopProduct::getStatus, 1) // 上架状态
                .gt(ShopProduct::getStock, 0) // 有库存
                .ne(ShopProduct::getId, productId); // 排除当前商品
        
        // 优先推荐同类目商品
        if (currentProduct != null && currentProduct.getCategoryId() != null) {
            wrapper.eq(ShopProduct::getCategoryId, currentProduct.getCategoryId());
        }
        
        // 按创建时间倒序（最新商品）
        wrapper.orderByDesc(ShopProduct::getCreateTime);
        wrapper.last("LIMIT " + limit);
        
        List<ShopProduct> products = shopProductMapper.selectList(wrapper);
        
        // 如果同类目商品不足，补充其他类目的商品
        if (products.size() < limit) {
            int remaining = limit - products.size();
            LambdaQueryWrapper<ShopProduct> otherWrapper = new LambdaQueryWrapper<>();
            otherWrapper.eq(ShopProduct::getStatus, 1)
                    .gt(ShopProduct::getStock, 0)
                    .ne(ShopProduct::getId, productId);
            
            if (currentProduct != null && currentProduct.getCategoryId() != null) {
                otherWrapper.ne(ShopProduct::getCategoryId, currentProduct.getCategoryId());
            }
            
            otherWrapper.orderByDesc(ShopProduct::getCreateTime);
            otherWrapper.last("LIMIT " + remaining);
            
            List<ShopProduct> otherProducts = shopProductMapper.selectList(otherWrapper);
            products.addAll(otherProducts);
        }
        
        return products.stream()
                .map(ShopProduct::getId)
                .collect(Collectors.toList());
    }
}

