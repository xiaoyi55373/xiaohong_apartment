package com.xiaohong.lease.web.app.service.impl;

import com.xiaohong.lease.common.cache.CacheConstant;
import com.xiaohong.lease.model.entity.LeaseTerm;
import com.xiaohong.lease.web.app.mapper.LeaseTermMapper;
import com.xiaohong.lease.web.app.service.LeaseTermService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 小红
* @description 针对表【lease_term(租期)】的数据库操作Service实现
* @createDate 2023-07-26 11:12:39
*/
@Service
@CacheConfig(cacheNames = "config")
public class LeaseTermServiceImpl extends ServiceImpl<LeaseTermMapper, LeaseTerm>
    implements LeaseTermService {

    @Autowired
    private LeaseTermMapper leaseTermMapper;

    /**
     * 根据房间ID查询租期（带缓存）
     */
    @Override
    @Cacheable(key = CacheConstant.LEASE_TERMS_KEY + ":#id")
    public List<LeaseTerm> listByRoomId(Long id) {
        return leaseTermMapper.selectListByRoomId(id);
    }
    
    /**
     * 获取所有租期列表（带缓存）
     * 缓存2小时
     */
    @Override
    @Cacheable(key = "'" + CacheConstant.LEASE_TERMS_KEY + "'")
    public List<LeaseTerm> list() {
        return super.list();
    }
    
    /**
     * 更新时清除缓存
     */
    @Override
    @CacheEvict(allEntries = true)
    public boolean updateById(LeaseTerm entity) {
        return super.updateById(entity);
    }
    
    /**
     * 保存时清除缓存
     */
    @Override
    @CacheEvict(allEntries = true)
    public boolean save(LeaseTerm entity) {
        return super.save(entity);
    }
}




