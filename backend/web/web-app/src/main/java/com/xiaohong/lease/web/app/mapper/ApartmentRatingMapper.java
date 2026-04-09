package com.xiaohong.lease.web.app.mapper;

import com.xiaohong.lease.model.entity.ApartmentRating;
import com.xiaohong.lease.web.app.vo.rating.RatingItemVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 公寓评分Mapper
 *
 * @author 小红
 * @description 针对表【apartment_rating(公寓评分记录表)】的数据库操作Mapper
 * @createDate 2026-04-03
 */
public interface ApartmentRatingMapper extends BaseMapper<ApartmentRating> {

    /**
     * 查询公寓评分列表
     */
    IPage<RatingItemVo> pageRatingItem(Page<RatingItemVo> page, @Param("apartmentId") Long apartmentId,
                                       @Param("filterType") Integer filterType, @Param("sortType") String sortType);

    /**
     * 查询公寓评分统计
     */
    Map<String, Object> selectRatingStatistics(@Param("apartmentId") Long apartmentId);

    /**
     * 查询各星级评分人数分布
     */
    List<Map<String, Object>> selectScoreDistribution(@Param("apartmentId") Long apartmentId);

    /**
     * 查询用户是否已评分
     */
    ApartmentRating selectByUserIdAndApartmentId(@Param("userId") Long userId, @Param("apartmentId") Long apartmentId);
}
