package com.xiaohong.lease.web.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaohong.lease.model.entity.SystemLoginLog;

public interface SystemLoginLogService extends IService<SystemLoginLog> {
    IPage<SystemLoginLog> pageItem(IPage<SystemLoginLog> page, String username, String createTimeBegin, String createTimeEnd);
}
