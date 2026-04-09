package com.xiaohong.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaohong.lease.model.entity.SystemUser;
import com.xiaohong.lease.web.admin.mapper.SystemUserMapper;
import com.xiaohong.lease.web.admin.service.SystemUserService;
import com.xiaohong.lease.web.admin.vo.system.user.SystemUserItemVo;
import com.xiaohong.lease.web.admin.vo.system.user.SystemUserQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {

    @Override
    public void saveOrUpdateSystemUser(SystemUser systemUser) {
        saveOrUpdate(systemUser);
    }

    @Override
    public IPage<SystemUserItemVo> pageItem(IPage<SystemUser> page, SystemUserQueryVo queryVo) {
        LambdaQueryWrapper<SystemUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(queryVo.getName() != null, SystemUser::getName, queryVo.getName());
        queryWrapper.like(queryVo.getPhone() != null, SystemUser::getPhone, queryVo.getPhone());
        
        IPage<SystemUser> userPage = page(page, queryWrapper);
        
        // 转换为VO
        List<SystemUserItemVo> records = new ArrayList<>();
        for (SystemUser user : userPage.getRecords()) {
            SystemUserItemVo vo = new SystemUserItemVo();
            BeanUtils.copyProperties(user, vo);
            records.add(vo);
        }
        
        IPage<SystemUserItemVo> resultPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
        resultPage.setCurrent(userPage.getCurrent());
        resultPage.setSize(userPage.getSize());
        resultPage.setTotal(userPage.getTotal());
        resultPage.setRecords(records);
        
        return resultPage;
    }

    @Override
    public SystemUserItemVo getSystemUserById(Long id) {
        SystemUser user = getById(id);
        if (user == null) {
            return null;
        }
        SystemUserItemVo vo = new SystemUserItemVo();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
