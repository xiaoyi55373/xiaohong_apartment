package com.xiaohong.lease.web.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaohong.lease.common.cache.CacheConstant;
import com.xiaohong.lease.model.entity.ProvinceInfo;
import com.xiaohong.lease.web.app.service.ProvinceInfoService;
import com.xiaohong.lease.web.app.mapper.ProvinceInfoMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 小红
* @description 针对表【province_info】的数据库操作Service实现
* @createDate 2023-07-26 11:12:39
*/
@Service
@CacheConfig(cacheNames = "region")
public class ProvinceInfoServiceImpl extends ServiceImpl<ProvinceInfoMapper, ProvinceInfo>
    implements ProvinceInfoService {

    /**
     * 获取所有省份列表（带缓存）
     * 缓存1小时
     */
    @Override
    @Cacheable(key = "'" + CacheConstant.REGION_PROVINCES_KEY + "'")
    public List<ProvinceInfo> list() {
        return super.list();
    }
    
    /**
     * 更新时清除缓存
     */
    @Override
    @CacheEvict(key = "'" + CacheConstant.REGION_PROVINCES_KEY + "'")
    public boolean updateById(ProvinceInfo entity) {
        return super.updateById(entity);
    }
    
    /**
     * 保存时清除缓存
     */
    @Override
    @CacheEvict(key = "'" + CacheConstant.REGION_PROVINCES_KEY + "'")
    public boolean save(ProvinceInfo entity) {
        return super.save(entity);
    }
    
    /**
     * 删除时清除缓存
     */
    @Override
    @CacheEvict(key = "'" + CacheConstant.REGION_PROVINCES_KEY + "'")
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
    }
}




