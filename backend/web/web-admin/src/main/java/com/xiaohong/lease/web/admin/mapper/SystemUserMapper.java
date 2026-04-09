package com.xiaohong.lease.web.admin.mapper;

import com.xiaohong.lease.model.entity.SystemUser;
import com.xiaohong.lease.web.admin.vo.system.user.SystemUserItemVo;
import com.xiaohong.lease.web.admin.vo.system.user.SystemUserQueryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
* @author 小红
* @description 针对表【system_user(员工信息表)】的数据库操作Mapper
* @createDate 2023-07-24 15:48:00
* @Entity com.xiaohong.lease.model.SystemUser
*/
public interface SystemUserMapper extends BaseMapper<SystemUser> {

    IPage<SystemUserItemVo> pageItem(IPage<SystemUser> page, SystemUserQueryVo queryVo);
}




