package com.xiaohong.lease.web.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaohong.lease.model.entity.SystemRole;

import java.util.List;

public interface SystemRoleService extends IService<SystemRole> {
    void updateMenuListById(Long roleId, List<Long> menuIdList);
}
