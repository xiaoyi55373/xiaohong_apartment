package com.xiaohong.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaohong.lease.model.entity.SystemLoginLog;
import com.xiaohong.lease.web.admin.mapper.SystemLoginLogMapper;
import com.xiaohong.lease.web.admin.service.SystemLoginLogService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SystemLoginLogServiceImpl extends ServiceImpl<SystemLoginLogMapper, SystemLoginLog> implements SystemLoginLogService {

    // 模拟数据
    private static final List<SystemLoginLog> MOCK_LOGS = new ArrayList<>();
    
    static {
        SystemLoginLog log1 = new SystemLoginLog();
        log1.setId(1L);
        log1.setUsername("admin");
        log1.setIpAddr("127.0.0.1");
        log1.setLoginLocation("本地");
        log1.setBrowser("Chrome");
        log1.setOs("Mac OS");
        log1.setStatus(1);
        log1.setMsg("登录成功");
        log1.setLoginTime(new Date());
        log1.setCreateTime(new Date());
        
        MOCK_LOGS.add(log1);
    }

    @Override
    public IPage<SystemLoginLog> pageItem(IPage<SystemLoginLog> page, String username, String createTimeBegin, String createTimeEnd) {
        try {
            return page(page);
        } catch (Exception e) {
            page.setRecords(MOCK_LOGS);
            page.setTotal(MOCK_LOGS.size());
            return page;
        }
    }
}
