package com.xiaohong.lease.web.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaohong.lease.model.entity.SystemDept;

import java.util.List;

public interface SystemDeptService extends IService<SystemDept> {
    List<SystemDept> listAsTree();
    void updateStatus(Long id, Integer status);
}
