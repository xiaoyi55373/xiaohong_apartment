package com.xiaohong.lease.web.app.service.impl;

import com.xiaohong.lease.common.cache.CacheConstant;
import com.xiaohong.lease.common.cache.CacheService;
import com.xiaohong.lease.model.entity.ApartmentInfo;
import com.xiaohong.lease.model.entity.ApartmentRating;
import com.xiaohong.lease.model.entity.UserInfo;
import com.xiaohong.lease.web.app.mapper.ApartmentInfoMapper;
import com.xiaohong.lease.web.app.mapper.ApartmentRatingMapper;
import com.xiaohong.lease.web.app.mapper.UserInfoMapper;
import com.xiaohong.lease.web.app.service.ApartmentRatingService;
import com.xiaohong.lease.web.app.vo.rating.RatingItemVo;
import com.xiaohong.lease.web.app.vo.rating.RatingQueryVo;
import com.xiaohong.lease.web.app.vo.rating.RatingStatisticsVo;
import com.xiaohong.lease.web.app.vo.rating.RatingSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公寓评分服务实现类
 *
 * @author 小红
 * @createDate 2026-04-03
 */
@Service
public class ApartmentRatingServiceImpl extends ServiceImpl<ApartmentRatingMapper, ApartmentRating>
        implements ApartmentRatingService {

    private static final Logger log = LoggerFactory.getLogger(ApartmentRatingServiceImpl.class);

    @Autowired
    private ApartmentRatingMapper apartmentRatingMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private CacheService cacheService;

    // 评分维度权重
    private static final BigDecimal ENVIRONMENT_WEIGHT = new BigDecimal("0.20");
    private static final BigDecimal TRAFFIC_WEIGHT = new BigDecimal("0.20");
    private static final BigDecimal FACILITY_WEIGHT = new BigDecimal("0.20");
    private static final BigDecimal SERVICE_WEIGHT = new BigDecimal("0.20");
    private static final BigDecimal VALUE_WEIGHT = new BigDecimal("0.20");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitRating(Long userId, RatingSubmitVo submitVo) {
        log.info("用户提交评分, userId: {}, apartmentId: {}", userId, submitVo.getApartmentId());

        // 1. 校验评分范围
        validateScore(submitVo.getEnvironmentScore(), "环境评分");
        validateScore(submitVo.getTrafficScore(), "交通评分");
        validateScore(submitVo.getFacilityScore(), "设施评分");
        validateScore(submitVo.getServiceScore(), "服务评分");
        validateScore(submitVo.getValueScore(), "性价比评分");

        // 2. 计算综合评分
        BigDecimal overallScore = calculateOverallScore(
                submitVo.getEnvironmentScore(),
                submitVo.getTrafficScore(),
                submitVo.getFacilityScore(),
                submitVo.getServiceScore(),
                submitVo.getValueScore()
        );

        // 3. 查询是否已评分
        ApartmentRating existingRating = apartmentRatingMapper.selectByUserIdAndApartmentId(
                userId, submitVo.getApartmentId());

        if (existingRating != null) {
            // 更新评分
            existingRating.setEnvironmentScore(submitVo.getEnvironmentScore());
            existingRating.setTrafficScore(submitVo.getTrafficScore());
            existingRating.setFacilityScore(submitVo.getFacilityScore());
            existingRating.setServiceScore(submitVo.getServiceScore());
            existingRating.setValueScore(submitVo.getValueScore());
            existingRating.setOverallScore(overallScore);
            existingRating.setContent(submitVo.getContent());
            if (submitVo.getIsAnonymous() != null) {
                existingRating.setIsAnonymous(submitVo.getIsAnonymous());
            }
            apartmentRatingMapper.updateById(existingRating);
            log.info("更新评分成功, ratingId: {}", existingRating.getId());
        } else {
            // 新增评分
            ApartmentRating rating = new ApartmentRating();
            rating.setUserId(userId);
            rating.setApartmentId(submitVo.getApartmentId());
            rating.setEnvironmentScore(submitVo.getEnvironmentScore());
            rating.setTrafficScore(submitVo.getTrafficScore());
            rating.setFacilityScore(submitVo.getFacilityScore());
            rating.setServiceScore(submitVo.getServiceScore());
            rating.setValueScore(submitVo.getValueScore());
            rating.setOverallScore(overallScore);
            rating.setContent(submitVo.getContent());
            rating.setIsAnonymous(submitVo.getIsAnonymous() != null ? submitVo.getIsAnonymous() : 0);
            rating.setStatus(1); // 默认已通过
            apartmentRatingMapper.insert(rating);
            log.info("新增评分成功, ratingId: {}", rating.getId());
        }

        // 4. 更新公寓平均评分
        updateApartmentAverageScore(submitVo.getApartmentId());

        // 5. 清除评分缓存
        clearRatingCache(submitVo.getApartmentId());
    }

    @Override
    public RatingStatisticsVo getRatingStatistics(Long userId, Long apartmentId) {
        log.info("获取公寓评分统计, apartmentId: {}", apartmentId);

        // 尝试从缓存获取
        String cacheKey = CacheConstant.buildKey("rating", "statistics", apartmentId);
        @SuppressWarnings("unchecked")
        Map<String, Object> cached = (Map<String, Object>) cacheService.get(cacheKey);
        if (cached != null) {
            RatingStatisticsVo vo = new RatingStatisticsVo();
            vo.setApartmentId(apartmentId);
            vo.setAvgOverallScore((BigDecimal) cached.get("avgOverallScore"));
            vo.setAvgEnvironmentScore((BigDecimal) cached.get("avgEnvironmentScore"));
            vo.setAvgTrafficScore((BigDecimal) cached.get("avgTrafficScore"));
            vo.setAvgFacilityScore((BigDecimal) cached.get("avgFacilityScore"));
            vo.setAvgServiceScore((BigDecimal) cached.get("avgServiceScore"));
            vo.setAvgValueScore((BigDecimal) cached.get("avgValueScore"));
            vo.setTotalCount((Integer) cached.get("totalCount"));
            vo.setScoreDistribution((Map<String, Integer>) cached.get("scoreDistribution"));
            vo.setDimensionScores((Map<String, BigDecimal>) cached.get("dimensionScores"));
            vo.setHasRated((Boolean) cached.get("hasRated"));
            vo.setUserRatingId((Long) cached.get("userRatingId"));
            return vo;
        }

        // 1. 查询统计数据
        Map<String, Object> statistics = apartmentRatingMapper.selectRatingStatistics(apartmentId);

        RatingStatisticsVo vo = new RatingStatisticsVo();
        vo.setApartmentId(apartmentId);

        if (statistics != null) {
            Object avgOverall = statistics.get("avg_overall_score");
            Object avgEnvironment = statistics.get("avg_environment_score");
            Object avgTraffic = statistics.get("avg_traffic_score");
            Object avgFacility = statistics.get("avg_facility_score");
            Object avgService = statistics.get("avg_service_score");
            Object avgValue = statistics.get("avg_value_score");
            Object totalCount = statistics.get("total_count");

            vo.setAvgOverallScore(toBigDecimal(avgOverall));
            vo.setAvgEnvironmentScore(toBigDecimal(avgEnvironment));
            vo.setAvgTrafficScore(toBigDecimal(avgTraffic));
            vo.setAvgFacilityScore(toBigDecimal(avgFacility));
            vo.setAvgServiceScore(toBigDecimal(avgService));
            vo.setAvgValueScore(toBigDecimal(avgValue));
            vo.setTotalCount(totalCount != null ? ((Number) totalCount).intValue() : 0);
        } else {
            vo.setAvgOverallScore(BigDecimal.ZERO);
            vo.setAvgEnvironmentScore(BigDecimal.ZERO);
            vo.setAvgTrafficScore(BigDecimal.ZERO);
            vo.setAvgFacilityScore(BigDecimal.ZERO);
            vo.setAvgServiceScore(BigDecimal.ZERO);
            vo.setAvgValueScore(BigDecimal.ZERO);
            vo.setTotalCount(0);
        }

        // 2. 查询星级分布
        List<Map<String, Object>> distributionList = apartmentRatingMapper.selectScoreDistribution(apartmentId);
        Map<String, Integer> scoreDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            scoreDistribution.put(String.valueOf(i), 0);
        }
        for (Map<String, Object> item : distributionList) {
            Object scoreObj = item.get("score_level") != null ? item.get("score_level") : item.get("SCORE_LEVEL");
            Object countObj = item.get("count") != null ? item.get("count") : item.get("COUNT");
            if (scoreObj == null || countObj == null) {
                continue;
            }
            String score = String.valueOf(((Number) scoreObj).intValue());
            Integer count = ((Number) countObj).intValue();
            scoreDistribution.put(score, count);
        }
        vo.setScoreDistribution(scoreDistribution);

        // 3. 维度评分占比
        Map<String, BigDecimal> dimensionScores = new HashMap<>();
        dimensionScores.put("environment", vo.getAvgEnvironmentScore());
        dimensionScores.put("traffic", vo.getAvgTrafficScore());
        dimensionScores.put("facility", vo.getAvgFacilityScore());
        dimensionScores.put("service", vo.getAvgServiceScore());
        dimensionScores.put("value", vo.getAvgValueScore());
        vo.setDimensionScores(dimensionScores);

        // 4. 当前用户是否已评分
        if (userId != null) {
            ApartmentRating userRating = apartmentRatingMapper.selectByUserIdAndApartmentId(userId, apartmentId);
            vo.setHasRated(userRating != null);
            vo.setUserRatingId(userRating != null ? userRating.getId() : null);
        } else {
            vo.setHasRated(false);
            vo.setUserRatingId(null);
        }

        // 缓存结果
        Map<String, Object> cacheMap = new HashMap<>();
        cacheMap.put("avgOverallScore", vo.getAvgOverallScore());
        cacheMap.put("avgEnvironmentScore", vo.getAvgEnvironmentScore());
        cacheMap.put("avgTrafficScore", vo.getAvgTrafficScore());
        cacheMap.put("avgFacilityScore", vo.getAvgFacilityScore());
        cacheMap.put("avgServiceScore", vo.getAvgServiceScore());
        cacheMap.put("avgValueScore", vo.getAvgValueScore());
        cacheMap.put("totalCount", vo.getTotalCount());
        cacheMap.put("scoreDistribution", vo.getScoreDistribution());
        cacheMap.put("dimensionScores", vo.getDimensionScores());
        cacheMap.put("hasRated", vo.getHasRated());
        cacheMap.put("userRatingId", vo.getUserRatingId());
        cacheService.set(cacheKey, cacheMap, CacheConstant.APARTMENT_DETAIL_EXPIRE);

        return vo;
    }

    @Override
    public IPage<RatingItemVo> pageRatingItem(Page<RatingItemVo> page, RatingQueryVo queryVo) {
        log.info("分页查询公寓评分列表, apartmentId: {}", queryVo.getApartmentId());
        return apartmentRatingMapper.pageRatingItem(page, queryVo.getApartmentId(),
                queryVo.getFilterType(), queryVo.getSortType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRating(Long userId, Long ratingId) {
        log.info("删除评分, userId: {}, ratingId: {}", userId, ratingId);

        ApartmentRating rating = apartmentRatingMapper.selectById(ratingId);
        if (rating == null) {
            throw new RuntimeException("评分记录不存在");
        }
        if (!rating.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除他人评分");
        }

        apartmentRatingMapper.deleteById(ratingId);

        // 更新公寓平均评分
        updateApartmentAverageScore(rating.getApartmentId());

        // 清除缓存
        clearRatingCache(rating.getApartmentId());
    }

    /**
     * 更新公寓平均评分
     */
    private void updateApartmentAverageScore(Long apartmentId) {
        Map<String, Object> statistics = apartmentRatingMapper.selectRatingStatistics(apartmentId);
        if (statistics == null) {
            return;
        }

        Object avgOverall = statistics.get("avg_overall_score");
        Object totalCount = statistics.get("total_count");

        BigDecimal avgScore = toBigDecimal(avgOverall);
        Integer scoreCount = totalCount != null ? ((Number) totalCount).intValue() : 0;

        ApartmentInfo apartmentInfo = new ApartmentInfo();
        apartmentInfo.setId(apartmentId);
        apartmentInfo.setAvgScore(avgScore);
        apartmentInfo.setScoreCount(scoreCount);
        apartmentInfoMapper.updateById(apartmentInfo);

        log.info("更新公寓平均评分, apartmentId: {}, avgScore: {}, scoreCount: {}",
                apartmentId, avgScore, scoreCount);
    }

    /**
     * 计算综合评分
     */
    private BigDecimal calculateOverallScore(BigDecimal environment, BigDecimal traffic,
                                             BigDecimal facility, BigDecimal service, BigDecimal value) {
        BigDecimal total = environment.multiply(ENVIRONMENT_WEIGHT)
                .add(traffic.multiply(TRAFFIC_WEIGHT))
                .add(facility.multiply(FACILITY_WEIGHT))
                .add(service.multiply(SERVICE_WEIGHT))
                .add(value.multiply(VALUE_WEIGHT));
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 校验评分范围
     */
    private void validateScore(BigDecimal score, String scoreName) {
        if (score == null || score.compareTo(BigDecimal.ONE) < 0 || score.compareTo(new BigDecimal("5")) > 0) {
            throw new RuntimeException(scoreName + "必须在1-5之间");
        }
    }

    /**
     * 清除评分缓存
     */
    private void clearRatingCache(Long apartmentId) {
        cacheService.delete(CacheConstant.buildKey("rating", "statistics", apartmentId));
        cacheService.deleteByPattern(CacheConstant.buildKey("rating", "list", apartmentId) + ":*");
    }

    /**
     * 转换为BigDecimal
     */
    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString()).setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(value.toString()).setScale(2, RoundingMode.HALF_UP);
    }
}
