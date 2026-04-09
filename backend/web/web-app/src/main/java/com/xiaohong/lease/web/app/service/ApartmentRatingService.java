package com.xiaohong.lease.web.app.service;

import com.xiaohong.lease.model.entity.ApartmentRating;
import com.xiaohong.lease.web.app.vo.rating.RatingItemVo;
import com.xiaohong.lease.web.app.vo.rating.RatingQueryVo;
import com.xiaohong.lease.web.app.vo.rating.RatingStatisticsVo;
import com.xiaohong.lease.web.app.vo.rating.RatingSubmitVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 公寓评分服务接口
 *
 * @author 小红
 * @createDate 2026-04-03
 */
public interface ApartmentRatingService extends IService<ApartmentRating> {

    /**
     * 提交评分
     *
     * @param userId 用户ID
     * @param submitVo 评分内容
     */
    void submitRating(Long userId, RatingSubmitVo submitVo);

    /**
     * 获取公寓评分统计
     *
     * @param userId 当前用户ID
     * @param apartmentId 公寓ID
     * @return 评分统计数据
     */
    RatingStatisticsVo getRatingStatistics(Long userId, Long apartmentId);

    /**
     * 分页查询公寓评分列表
     *
     * @param page 分页参数
     * @param queryVo 查询条件
     * @return 评分列表
     */
    IPage<RatingItemVo> pageRatingItem(Page<RatingItemVo> page, RatingQueryVo queryVo);

    /**
     * 删除评分
     *
     * @param userId 用户ID
     * @param ratingId 评分ID
     */
    void deleteRating(Long userId, Long ratingId);
}
