package com.xiaohong.lease.web.app.service.impl;

import com.xiaohong.lease.model.entity.BrowsingHistory;
import com.xiaohong.lease.web.app.mapper.BrowsingHistoryMapper;
import com.xiaohong.lease.web.app.service.BrowsingHistoryService;
import com.xiaohong.lease.web.app.vo.history.HistoryItemVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author 小红
 * @description 针对表【browsing_history(浏览历史)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class BrowsingHistoryServiceImpl extends ServiceImpl<BrowsingHistoryMapper, BrowsingHistory>
        implements BrowsingHistoryService {
    private static final Logger log = LoggerFactory.getLogger(BrowsingHistoryServiceImpl.class);

    @Autowired
    private BrowsingHistoryMapper browsingHistoryMapper;

    @Override
    public IPage<HistoryItemVo> pageItem(Page<HistoryItemVo> page, Long userId) {
        return browsingHistoryMapper.pageItem(page, userId);
    }

    @Override
    @Async
    public void saveBrowsingHistory(Long userId, Long roomId) {

        log.info("保存浏览历史");

        LambdaQueryWrapper<BrowsingHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BrowsingHistory::getUserId, userId);
        queryWrapper.eq(BrowsingHistory::getRoomId, roomId);
        BrowsingHistory browsingHistory = browsingHistoryMapper.selectOne(queryWrapper);

        if (browsingHistory != null) {
            browsingHistory.setBrowseTime(new Date());
            browsingHistoryMapper.updateById(browsingHistory);
        } else {
            BrowsingHistory newHistory = new BrowsingHistory();
            newHistory.setRoomId(roomId);
            newHistory.setUserId(userId);
            newHistory.setBrowseTime(new Date());
            browsingHistoryMapper.insert(newHistory);
        }

    }
}




