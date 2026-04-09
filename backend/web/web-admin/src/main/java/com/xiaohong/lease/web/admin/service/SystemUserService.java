package com.xiaohong.lease.web.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaohong.lease.model.entity.SystemUser;
import com.xiaohong.lease.web.admin.vo.system.user.SystemUserItemVo;
import com.xiaohong.lease.web.admin.vo.system.user.SystemUserQueryVo;

public interface SystemUserService extends IService<SystemUser> {
    
    void saveOrUpdateSystemUser(SystemUser systemUser);
    
    IPage<SystemUserItemVo> pageItem(IPage<SystemUser> page, SystemUserQueryVo queryVo);
    
    SystemUserItemVo getSystemUserById(Long id);
}
