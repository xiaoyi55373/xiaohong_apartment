package com.xiaohong.lease.web.app.service.impl;

import com.xiaohong.lease.common.cache.CacheConstant;
import com.xiaohong.lease.common.cache.CacheService;
import com.xiaohong.lease.model.entity.*;
import com.xiaohong.lease.model.enums.ItemType;
import com.xiaohong.lease.web.app.mapper.*;
import com.xiaohong.lease.web.app.service.RecommendService;
import com.xiaohong.lease.web.app.vo.graph.GraphVo;
import com.xiaohong.lease.web.app.vo.recommend.RecommendQueryVo;
import com.xiaohong.lease.web.app.vo.recommend.RecommendRoomVo;
import com.xiaohong.lease.web.app.vo.recommend.UserPreferenceVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐服务实现类
 * 实现基于内容的推荐和基于协同过滤的推荐算法
 * 
 * @author 小红
 * @createDate 2026-04-03
 */
@Service
public class RecommendServiceImpl implements RecommendService {

    private static final Logger log = LoggerFactory.getLogger(RecommendServiceImpl.class);

    // 推荐类型常量
    private static final String RECOMMEND_TYPE_CONTENT = "CONTENT";
    private static final String RECOMMEND_TYPE_COLLABORATIVE = "COLLABORATIVE";
    private static final String RECOMMEND_TYPE_HOT = "HOT";

    // 推荐得分权重
    private static final double WEIGHT_CONTENT = 0.5;
    private static final double WEIGHT_COLLABORATIVE = 0.3;
    private static final double WEIGHT_HOT = 0.2;

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private BrowsingHistoryMapper browsingHistoryMapper;

    @Autowired
    private RoomLabelMapper roomLabelMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private UserBehaviorLogMapper userBehaviorLogMapper;

    @Autowired
    private CacheService cacheService;

    @Override
    public List<RecommendRoomVo> getRecommendRooms(Long userId, RecommendQueryVo queryVo) {
        log.info("获取个性化推荐房间列表, userId: {}, limit: {}", userId, queryVo.getLimit());

        Integer limit = queryVo.getLimit() != null ? queryVo.getLimit() : 10;
        String type = queryVo.getType() != null ? queryVo.getType() : "ALL";

        // 尝试从缓存获取
        String cacheKey = CacheConstant.RECOMMENDATION_USER + userId + ":" + type + ":" + limit;
        @SuppressWarnings("unchecked")
        List<RecommendRoomVo> cachedRecommendations = (List<RecommendRoomVo>) cacheService.get(cacheKey);
        if (cachedRecommendations != null && !cachedRecommendations.isEmpty()) {
            log.info("从缓存获取推荐结果, userId: {}", userId);
            return cachedRecommendations;
        }

        List<RecommendRoomVo> result = new ArrayList<>();
        Set<Long> recommendedRoomIds = new HashSet<>();

        // 根据类型获取推荐
        switch (type) {
            case "CONTENT":
                result = getContentBasedRecommendations(userId, limit);
                break;
            case "COLLABORATIVE":
                result = getCollaborativeRecommendations(userId, limit);
                break;
            case "HOT":
                result = getHotRecommendations(limit);
                break;
            default:
                // 混合推荐策略
                result = getHybridRecommendations(userId, limit, queryVo.getIncludeViewed());
                break;
        }

        // 缓存结果
        if (!result.isEmpty()) {
            cacheService.set(cacheKey, result, CacheConstant.RECOMMENDATION_EXPIRE);
        }

        return result;
    }

    /**
     * 混合推荐策略
     * 结合基于内容、协同过滤和热门推荐
     */
    private List<RecommendRoomVo> getHybridRecommendations(Long userId, Integer limit, Boolean includeViewed) {
        List<RecommendRoomVo> hybridResults = new ArrayList<>();
        Map<Long, RecommendRoomVo> roomMap = new HashMap<>();

        // 1. 获取基于内容的推荐 (50%)
        int contentLimit = (int) Math.ceil(limit * WEIGHT_CONTENT);
        List<RecommendRoomVo> contentRecommendations = getContentBasedRecommendations(userId, contentLimit * 2);
        for (RecommendRoomVo room : contentRecommendations) {
            room.setRecommendScore(room.getRecommendScore() * WEIGHT_CONTENT);
            roomMap.put(room.getId(), room);
        }

        // 2. 获取基于协同过滤的推荐 (30%)
        int collabLimit = (int) Math.ceil(limit * WEIGHT_COLLABORATIVE);
        List<RecommendRoomVo> collabRecommendations = getCollaborativeRecommendations(userId, collabLimit * 2);
        for (RecommendRoomVo room : collabRecommendations) {
            if (roomMap.containsKey(room.getId())) {
                // 如果已存在，增加权重
                RecommendRoomVo existing = roomMap.get(room.getId());
                existing.setRecommendScore(existing.getRecommendScore() + room.getRecommendScore() * WEIGHT_COLLABORATIVE);
                existing.setRecommendType(existing.getRecommendType() + "," + room.getRecommendType());
            } else {
                room.setRecommendScore(room.getRecommendScore() * WEIGHT_COLLABORATIVE);
                roomMap.put(room.getId(), room);
            }
        }

        // 3. 获取热门推荐 (20%)
        int hotLimit = (int) Math.ceil(limit * WEIGHT_HOT);
        List<RecommendRoomVo> hotRecommendations = getHotRecommendations(hotLimit * 2);
        for (RecommendRoomVo room : hotRecommendations) {
            if (roomMap.containsKey(room.getId())) {
                RecommendRoomVo existing = roomMap.get(room.getId());
                existing.setRecommendScore(existing.getRecommendScore() + room.getRecommendScore() * WEIGHT_HOT);
            } else {
                room.setRecommendScore(room.getRecommendScore() * WEIGHT_HOT);
                roomMap.put(room.getId(), room);
            }
        }

        // 4. 合并并排序
        hybridResults.addAll(roomMap.values());
        hybridResults.sort((a, b) -> Double.compare(b.getRecommendScore(), a.getRecommendScore()));

        // 5. 过滤已浏览的房间
        if (!includeViewed) {
            Set<Long> viewedRoomIds = getUserViewedRoomIds(userId);
            hybridResults = hybridResults.stream()
                    .filter(r -> !viewedRoomIds.contains(r.getId()))
                    .collect(Collectors.toList());
        }

        // 6. 限制返回数量
        return hybridResults.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<RecommendRoomVo> getContentBasedRecommendations(Long userId, Integer limit) {
        log.info("获取基于内容的推荐, userId: {}, limit: {}", userId, limit);

        // 1. 获取用户偏好
        UserPreferenceVo preference = getUserPreference(userId);

        if (preference.getBrowseCount() == 0) {
            // 新用户，返回热门推荐
            return getHotRecommendations(limit);
        }

        // 2. 获取候选房间
        List<RoomInfo> candidateRooms = getCandidateRooms();

        // 3. 计算每个房间的推荐得分
        List<RecommendRoomVo> scoredRooms = new ArrayList<>();
        for (RoomInfo room : candidateRooms) {
            double score = calculateContentScore(room, preference);
            if (score > 0) {
                RecommendRoomVo recommendRoom = convertToRecommendRoomVo(room);
                recommendRoom.setRecommendScore(score);
                recommendRoom.setRecommendType(RECOMMEND_TYPE_CONTENT);
                recommendRoom.setRecommendReason(generateRecommendReason(room, preference));
                scoredRooms.add(recommendRoom);
            }
        }

        // 4. 排序并返回
        scoredRooms.sort((a, b) -> Double.compare(b.getRecommendScore(), a.getRecommendScore()));
        return scoredRooms.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * 计算基于内容的推荐得分
     */
    private double calculateContentScore(RoomInfo room, UserPreferenceVo preference) {
        double score = 0.0;

        // 获取房间完整信息
        ApartmentInfo apartment = apartmentInfoMapper.selectById(room.getApartmentId());
        if (apartment == null) {
            return 0.0;
        }

        // 1. 区域匹配得分 (权重: 0.3)
        if (preference.getDistrictWeights() != null && !preference.getDistrictWeights().isEmpty()) {
            Double districtWeight = preference.getDistrictWeights().get(apartment.getDistrictId());
            if (districtWeight != null) {
                score += districtWeight * 0.3;
            }
        }

        // 2. 价格匹配得分 (权重: 0.25)
        if (preference.getAvgRent() != null && preference.getAvgRent().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rentDiff = room.getRent().subtract(preference.getAvgRent()).abs();
            BigDecimal percentDiff = rentDiff.divide(preference.getAvgRent(), 4, RoundingMode.HALF_UP);
            double priceScore = Math.max(0, 1 - percentDiff.doubleValue());
            score += priceScore * 0.25;
        }

        // 3. 标签匹配得分 (权重: 0.25)
        List<LabelInfo> roomLabels = getRoomLabels(room.getId());
        if (preference.getLabelWeights() != null && !preference.getLabelWeights().isEmpty()) {
            double labelScore = 0.0;
            for (LabelInfo label : roomLabels) {
                Double weight = preference.getLabelWeights().get(label.getName());
                if (weight != null) {
                    labelScore += weight;
                }
            }
            score += Math.min(labelScore, 1.0) * 0.25;
        }

        // 4. 新鲜度得分 (权重: 0.1)
        // 新发布的房源得分更高
        if (room.getCreateTime() != null) {
            long daysSinceCreated = (System.currentTimeMillis() - room.getCreateTime().getTime()) / (1000 * 60 * 60 * 24);
            double freshnessScore = Math.max(0, 1 - (daysSinceCreated / 30.0)); // 30天内逐渐降低
            score += freshnessScore * 0.1;
        }

        // 5. 热度得分 (权重: 0.1)
        double hotScore = getRoomHotScore(room.getId());
        score += hotScore * 0.1;

        // 6. 评分得分 (权重: 0.1)
        double ratingScore = getRoomRatingScore(room.getApartmentId());
        score += ratingScore * 0.1;

        return score;
    }

    @Override
    public List<RecommendRoomVo> getCollaborativeRecommendations(Long userId, Integer limit) {
        log.info("获取基于协同过滤的推荐, userId: {}, limit: {}", userId, limit);

        // 1. 获取当前用户浏览过的房间
        List<BrowsingHistory> userHistory = getUserBrowsingHistory(userId);
        if (userHistory.isEmpty()) {
            return getHotRecommendations(limit);
        }

        Set<Long> userRoomIds = userHistory.stream()
                .map(BrowsingHistory::getRoomId)
                .collect(Collectors.toSet());

        // 2. 找到相似用户（浏览过相同房间的其他用户）
        Map<Long, Double> similarUsers = findSimilarUsers(userId, userRoomIds);

        if (similarUsers.isEmpty()) {
            return getHotRecommendations(limit);
        }

        // 3. 获取相似用户浏览过但当前用户未浏览的房间
        Map<Long, Double> candidateRoomScores = new HashMap<>();
        for (Map.Entry<Long, Double> entry : similarUsers.entrySet()) {
            Long similarUserId = entry.getKey();
            Double similarity = entry.getValue();

            List<BrowsingHistory> similarUserHistory = getUserBrowsingHistory(similarUserId);
            for (BrowsingHistory history : similarUserHistory) {
                Long roomId = history.getRoomId();
                if (!userRoomIds.contains(roomId)) {
                    // 根据相似度和浏览时间计算得分
                    double timeWeight = calculateTimeWeight(history.getBrowseTime());
                    double score = similarity * timeWeight;
                    candidateRoomScores.merge(roomId, score, Double::sum);
                }
            }
        }

        // 4. 转换为推荐结果
        List<RecommendRoomVo> result = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : candidateRoomScores.entrySet()) {
            RoomInfo room = roomInfoMapper.selectById(entry.getKey());
            if (room != null && isRoomAvailable(room)) {
                RecommendRoomVo recommendRoom = convertToRecommendRoomVo(room);
                recommendRoom.setRecommendScore(entry.getValue());
                recommendRoom.setRecommendType(RECOMMEND_TYPE_COLLABORATIVE);
                recommendRoom.setRecommendReason("看过该房源的人还看过");
                result.add(recommendRoom);
            }
        }

        // 5. 排序并返回
        result.sort((a, b) -> Double.compare(b.getRecommendScore(), a.getRecommendScore()));
        return result.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * 查找相似用户
     */
    private Map<Long, Double> findSimilarUsers(Long userId, Set<Long> userRoomIds) {
        Map<Long, Double> similarUsers = new HashMap<>();

        // 查询浏览过相同房间的其他用户
        for (Long roomId : userRoomIds) {
            LambdaQueryWrapper<BrowsingHistory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BrowsingHistory::getRoomId, roomId)
                    .ne(BrowsingHistory::getUserId, userId);
            List<BrowsingHistory> otherUsersHistory = browsingHistoryMapper.selectList(wrapper);

            for (BrowsingHistory history : otherUsersHistory) {
                Long otherUserId = history.getUserId();
                similarUsers.merge(otherUserId, 1.0, Double::sum);
            }
        }

        // 计算Jaccard相似度
        Map<Long, Double> similarities = new HashMap<>();
        for (Map.Entry<Long, Double> entry : similarUsers.entrySet()) {
            Long otherUserId = entry.getKey();
            Double commonRooms = entry.getValue();

            List<BrowsingHistory> otherUserHistory = getUserBrowsingHistory(otherUserId);
            Set<Long> otherUserRoomIds = otherUserHistory.stream()
                    .map(BrowsingHistory::getRoomId)
                    .collect(Collectors.toSet());

            // Jaccard相似度 = 共同浏览房间数 / 总浏览房间数
            Set<Long> union = new HashSet<>(userRoomIds);
            union.addAll(otherUserRoomIds);
            double jaccardSimilarity = commonRooms / union.size();

            similarities.put(otherUserId, jaccardSimilarity);
        }

        // 返回相似度大于阈值的用户
        return similarities.entrySet().stream()
                .filter(e -> e.getValue() > 0.1) // 相似度阈值
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(20) // 最多20个相似用户
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * 计算时间权重（越近权重越高）
     */
    private double calculateTimeWeight(Date browseTime) {
        if (browseTime == null) {
            return 0.5;
        }
        long daysDiff = (System.currentTimeMillis() - browseTime.getTime()) / (1000 * 60 * 60 * 24);
        return Math.exp(-daysDiff / 7.0); // 指数衰减，7天减半
    }

    @Override
    public List<RecommendRoomVo> getHotRecommendations(Integer limit) {
        log.info("获取热门推荐, limit: {}", limit);

        // 从缓存获取
        String cacheKey = CacheConstant.RECOMMENDATION_HOT + limit;
        @SuppressWarnings("unchecked")
        List<RecommendRoomVo> cached = (List<RecommendRoomVo>) cacheService.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // 获取热门房间（根据浏览次数）
        List<Map<String, Object>> hotRooms = browsingHistoryMapper.selectHotRooms(limit * 2);

        List<RecommendRoomVo> result = new ArrayList<>();
        for (Map<String, Object> record : hotRooms) {
            Object roomIdObj = record.get("room_id") != null ? record.get("room_id") : record.get("ROOM_ID");
            Object viewCountObj = record.get("view_count") != null ? record.get("view_count") : record.get("VIEW_COUNT");
            if (roomIdObj == null || viewCountObj == null) {
                continue;
            }
            Long roomId = ((Number) roomIdObj).longValue();
            Long viewCount = ((Number) viewCountObj).longValue();

            RoomInfo room = roomInfoMapper.selectById(roomId);
            if (room != null && isRoomAvailable(room)) {
                RecommendRoomVo recommendRoom = convertToRecommendRoomVo(room);
                // 热度得分归一化到0-1
                double hotScore = Math.min(viewCount / 100.0, 1.0);
                recommendRoom.setRecommendScore(hotScore);
                recommendRoom.setRecommendType(RECOMMEND_TYPE_HOT);
                recommendRoom.setRecommendReason("热门房源");
                result.add(recommendRoom);
            }
        }

        // 如果热门房间不足，补充最新发布的房间
        if (result.size() < limit) {
            int needMore = limit - result.size();
            List<RoomInfo> latestRooms = getLatestRooms(needMore);
            Set<Long> existingIds = result.stream()
                    .map(RecommendRoomVo::getId)
                    .collect(Collectors.toSet());

            for (RoomInfo room : latestRooms) {
                if (!existingIds.contains(room.getId())) {
                    RecommendRoomVo recommendRoom = convertToRecommendRoomVo(room);
                    recommendRoom.setRecommendScore(0.5);
                    recommendRoom.setRecommendType(RECOMMEND_TYPE_HOT);
                    recommendRoom.setRecommendReason("新上房源");
                    result.add(recommendRoom);
                }
            }
        }

        // 缓存结果
        cacheService.set(cacheKey, result, CacheConstant.RECOMMENDATION_EXPIRE);

        return result.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<RecommendRoomVo> getSimilarRooms(Long roomId, Integer limit) {
        log.info("获取相似房间推荐, roomId: {}, limit: {}", roomId, limit);

        RoomInfo targetRoom = roomInfoMapper.selectById(roomId);
        if (targetRoom == null) {
            return new ArrayList<>();
        }

        ApartmentInfo targetApartment = apartmentInfoMapper.selectById(targetRoom.getApartmentId());
        List<LabelInfo> targetLabels = getRoomLabels(roomId);
        Set<String> targetLabelNames = targetLabels.stream()
                .map(LabelInfo::getName)
                .collect(Collectors.toSet());

        // 获取候选房间
        List<RoomInfo> candidateRooms = getCandidateRooms();

        List<RecommendRoomVo> result = new ArrayList<>();
        for (RoomInfo room : candidateRooms) {
            if (room.getId().equals(roomId)) {
                continue; // 排除自身
            }

            double score = 0.0;

            // 1. 同公寓得分
            if (room.getApartmentId().equals(targetRoom.getApartmentId())) {
                score += 0.4;
            }

            // 2. 同区域得分
            ApartmentInfo apartment = apartmentInfoMapper.selectById(room.getApartmentId());
            if (apartment != null && targetApartment != null &&
                    apartment.getDistrictId().equals(targetApartment.getDistrictId())) {
                score += 0.2;
            }

            // 3. 价格相似度
            BigDecimal rentDiff = room.getRent().subtract(targetRoom.getRent()).abs();
            BigDecimal percentDiff = rentDiff.divide(targetRoom.getRent(), 4, RoundingMode.HALF_UP);
            score += Math.max(0, 0.2 - percentDiff.doubleValue() * 0.2);

            // 4. 标签相似度
            List<LabelInfo> roomLabels = getRoomLabels(room.getId());
            long commonLabels = roomLabels.stream()
                    .map(LabelInfo::getName)
                    .filter(targetLabelNames::contains)
                    .count();
            score += Math.min(commonLabels * 0.1, 0.2);

            if (score > 0.2) {
                RecommendRoomVo recommendRoom = convertToRecommendRoomVo(room);
                recommendRoom.setRecommendScore(score);
                recommendRoom.setRecommendType(RECOMMEND_TYPE_CONTENT);
                recommendRoom.setRecommendReason("相似房源");
                result.add(recommendRoom);
            }
        }

        result.sort((a, b) -> Double.compare(b.getRecommendScore(), a.getRecommendScore()));
        return result.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public UserPreferenceVo getUserPreference(Long userId) {
        log.info("获取用户偏好, userId: {}", userId);

        // 从缓存获取
        String cacheKey = CacheConstant.RECOMMENDATION_USER_PREF + userId;
        UserPreferenceVo cached = (UserPreferenceVo) cacheService.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        UserPreferenceVo preference = new UserPreferenceVo();
        preference.setUserId(userId);

        // 获取用户浏览历史
        List<BrowsingHistory> history = getUserBrowsingHistory(userId);
        preference.setBrowseCount(history.size());

        if (history.isEmpty()) {
            cacheService.set(cacheKey, preference, CacheConstant.RECOMMENDATION_EXPIRE);
            return preference;
        }

        // 分析区域偏好
        Map<Long, Integer> districtCount = new HashMap<>();
        Map<Long, Integer> cityCount = new HashMap<>();
        List<BigDecimal> rents = new ArrayList<>();
        Map<String, Integer> labelCount = new HashMap<>();

        for (BrowsingHistory record : history) {
            RoomInfo room = roomInfoMapper.selectById(record.getRoomId());
            if (room == null) continue;

            rents.add(room.getRent());

            ApartmentInfo apartment = apartmentInfoMapper.selectById(room.getApartmentId());
            if (apartment != null) {
                districtCount.merge(apartment.getDistrictId(), 1, Integer::sum);
                cityCount.merge(apartment.getCityId(), 1, Integer::sum);
            }

            List<LabelInfo> labels = getRoomLabels(room.getId());
            for (LabelInfo label : labels) {
                labelCount.merge(label.getName(), 1, Integer::sum);
            }
        }

        // 计算权重
        preference.setDistrictWeights(calculateWeights(districtCount));
        preference.setPreferredDistrictIds(new ArrayList<>(districtCount.keySet()));
        preference.setPreferredCityIds(new ArrayList<>(cityCount.keySet()));
        preference.setLabelWeights(calculateWeights(labelCount));
        preference.setPreferredLabels(new ArrayList<>(labelCount.keySet()));

        // 计算价格范围
        if (!rents.isEmpty()) {
            BigDecimal sum = rents.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            preference.setAvgRent(sum.divide(new BigDecimal(rents.size()), 2, RoundingMode.HALF_UP));

            BigDecimal min = rents.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            BigDecimal max = rents.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            preference.setMinRent(min.multiply(new BigDecimal("0.8")));
            preference.setMaxRent(max.multiply(new BigDecimal("1.2")));
        }

        // 缓存结果
        cacheService.set(cacheKey, preference, CacheConstant.RECOMMENDATION_EXPIRE);

        return preference;
    }

    @Override
    public void refreshUserRecommendations(Long userId) {
        log.info("刷新用户推荐缓存, userId: {}", userId);

        // 清除用户相关缓存
        cacheService.deleteByPattern(CacheConstant.RECOMMENDATION_USER + userId + ":*");
        cacheService.delete(CacheConstant.RECOMMENDATION_USER_PREF + userId);

        // 重新生成推荐
        RecommendQueryVo queryVo = new RecommendQueryVo();
        queryVo.setLimit(10);
        getRecommendRooms(userId, queryVo);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 获取用户浏览过的房间ID集合
     */
    private Set<Long> getUserViewedRoomIds(Long userId) {
        List<BrowsingHistory> history = getUserBrowsingHistory(userId);
        return history.stream()
                .map(BrowsingHistory::getRoomId)
                .collect(Collectors.toSet());
    }

    /**
     * 获取用户浏览历史
     */
    private List<BrowsingHistory> getUserBrowsingHistory(Long userId) {
        LambdaQueryWrapper<BrowsingHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowsingHistory::getUserId, userId)
                .orderByDesc(BrowsingHistory::getBrowseTime);
        return browsingHistoryMapper.selectList(wrapper);
    }

    /**
     * 获取候选房间列表
     */
    private List<RoomInfo> getCandidateRooms() {
        LambdaQueryWrapper<RoomInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomInfo::getIsRelease, 1)
                .eq(RoomInfo::getIsDeleted, 0)
                .orderByDesc(RoomInfo::getCreateTime);
        return roomInfoMapper.selectList(wrapper);
    }

    /**
     * 获取最新发布的房间
     */
    private List<RoomInfo> getLatestRooms(Integer limit) {
        LambdaQueryWrapper<RoomInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomInfo::getIsRelease, 1)
                .eq(RoomInfo::getIsDeleted, 0)
                .orderByDesc(RoomInfo::getCreateTime)
                .last("LIMIT " + limit);
        return roomInfoMapper.selectList(wrapper);
    }

    /**
     * 获取房间标签
     */
    private List<LabelInfo> getRoomLabels(Long roomId) {
        LambdaQueryWrapper<RoomLabel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomLabel::getRoomId, roomId);
        List<RoomLabel> roomLabels = roomLabelMapper.selectList(wrapper);

        if (roomLabels.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> labelIds = roomLabels.stream()
                .map(RoomLabel::getLabelId)
                .collect(Collectors.toList());

        return labelInfoMapper.selectBatchIds(labelIds);
    }

    /**
     * 获取房间热度得分
     */
    private double getRoomHotScore(Long roomId) {
        LambdaQueryWrapper<BrowsingHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrowsingHistory::getRoomId, roomId);
        Long count = browsingHistoryMapper.selectCount(wrapper);
        return Math.min(count / 50.0, 1.0); // 归一化到0-1
    }

    /**
     * 获取公寓评分得分
     */
    private double getRoomRatingScore(Long apartmentId) {
        ApartmentInfo apartment = apartmentInfoMapper.selectById(apartmentId);
        if (apartment == null || apartment.getAvgScore() == null) {
            return 0.5; // 默认中等评分
        }
        // 评分归一化到0-1 (假设最高5分)
        double normalizedScore = apartment.getAvgScore().doubleValue() / 5.0;
        // 评分人数加权 (人数越多越可信)
        int count = apartment.getScoreCount() != null ? apartment.getScoreCount() : 0;
        double countWeight = Math.min(count / 10.0, 1.0); // 10个评分达到最大权重
        return normalizedScore * 0.7 + countWeight * 0.3;
    }

    /**
     * 检查房间是否可用
     */
    private boolean isRoomAvailable(RoomInfo room) {
        return room.getIsRelease() != null && room.getIsRelease().getCode() == 1
                && room.getIsDeleted() != null && room.getIsDeleted() == 0;
    }

    /**
     * 转换RoomInfo为RecommendRoomVo
     */
    private RecommendRoomVo convertToRecommendRoomVo(RoomInfo room) {
        RecommendRoomVo vo = new RecommendRoomVo();
        BeanUtils.copyProperties(room, vo);

        // 获取公寓信息
        ApartmentInfo apartment = apartmentInfoMapper.selectById(room.getApartmentId());
        vo.setApartmentInfo(apartment);

        // 获取图片
        LambdaQueryWrapper<GraphInfo> graphWrapper = new LambdaQueryWrapper<>();
        graphWrapper.eq(GraphInfo::getItemType, ItemType.ROOM)
                .eq(GraphInfo::getItemId, room.getId());
        List<GraphInfo> graphs = graphInfoMapper.selectList(graphWrapper);
        List<GraphVo> graphVos = graphs.stream().map(g -> {
            GraphVo graphVo = new GraphVo();
            graphVo.setName(g.getName());
            graphVo.setUrl(g.getUrl());
            return graphVo;
        }).collect(Collectors.toList());
        vo.setGraphVoList(graphVos);

        // 获取标签
        vo.setLabelInfoList(getRoomLabels(room.getId()));

        return vo;
    }

    /**
     * 生成推荐原因
     */
    private String generateRecommendReason(RoomInfo room, UserPreferenceVo preference) {
        List<String> reasons = new ArrayList<>();

        ApartmentInfo apartment = apartmentInfoMapper.selectById(room.getApartmentId());
        if (apartment != null && preference.getPreferredDistrictIds() != null &&
                preference.getPreferredDistrictIds().contains(apartment.getDistrictId())) {
            reasons.add("您常看的" + apartment.getDistrictName() + "区域");
        }

        if (preference.getAvgRent() != null) {
            BigDecimal diff = room.getRent().subtract(preference.getAvgRent()).abs();
            if (diff.compareTo(preference.getAvgRent().multiply(new BigDecimal("0.2"))) < 0) {
                reasons.add("符合您的预算");
            }
        }

        List<LabelInfo> labels = getRoomLabels(room.getId());
        if (preference.getPreferredLabels() != null) {
            for (LabelInfo label : labels) {
                if (preference.getPreferredLabels().contains(label.getName())) {
                    reasons.add("包含您喜欢的" + label.getName() + "标签");
                    break;
                }
            }
        }

        if (reasons.isEmpty()) {
            return "为您推荐";
        }

        return String.join("，", reasons);
    }

    /**
     * 计算权重Map
     */
    private <T> Map<T, Double> calculateWeights(Map<T, Integer> countMap) {
        if (countMap.isEmpty()) {
            return new HashMap<>();
        }

        int total = countMap.values().stream().mapToInt(Integer::intValue).sum();
        Map<T, Double> weights = new HashMap<>();
        for (Map.Entry<T, Integer> entry : countMap.entrySet()) {
            weights.put(entry.getKey(), entry.getValue() / (double) total);
        }
        return weights;
    }
}
