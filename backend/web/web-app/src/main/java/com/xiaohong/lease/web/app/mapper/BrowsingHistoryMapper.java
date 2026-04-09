package com.xiaohong.lease.web.app.mapper;

import com.xiaohong.lease.model.entity.BrowsingHistory;
import com.xiaohong.lease.web.app.vo.history.HistoryItemVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

/**
* @author 小红
* @description 针对表【browsing_history(浏览历史)】的数据库操作Mapper
* @createDate 2023-07-26 11:12:39
* @Entity com.xiaohong.lease.model.entity.BrowsingHistory
*/
public interface BrowsingHistoryMapper extends BaseMapper<BrowsingHistory> {

    IPage<HistoryItemVo> pageItem(Page<HistoryItemVo> page, Long userId);

    /**
     * 查询热门房间（按浏览次数排序）
     *
     * @param limit 查询数量
     * @return 热门房间列表
     */
    List<Map<String, Object>> selectHotRooms(Integer limit);
}




