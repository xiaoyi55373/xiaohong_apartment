package com.xiaohong.lease.web.admin.mapper;

import com.xiaohong.lease.model.entity.AttrValue;
import com.xiaohong.lease.web.admin.vo.attr.AttrValueVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


public interface AttrValueMapper extends BaseMapper<AttrValue> {

    List<AttrValueVo> selectListByRoomId(Long id);
}




