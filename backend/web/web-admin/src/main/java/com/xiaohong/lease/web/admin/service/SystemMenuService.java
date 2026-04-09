package com.xiaohong.lease.web.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaohong.lease.model.entity.SystemMenu;

import java.util.List;

public interface SystemMenuService extends IService<SystemMenu> {
    List<SystemMenu> listAsTree();
    List<SystemMenu> listByRoleId(Long roleId);
}
