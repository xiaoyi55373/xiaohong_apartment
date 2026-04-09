package com.xiaohong.lease.web.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaohong.lease.common.cache.CacheConstant;
import com.xiaohong.lease.model.entity.CityInfo;
import com.xiaohong.lease.web.app.service.CityInfoService;
import com.xiaohong.lease.web.app.mapper.CityInfoMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 小红
* @description 针对表【city_info】的数据库操作Service实现
* @createDate 2023-07-26 11:12:39
*/
@Service
@CacheConfig(cacheNames = "region")
public class CityInfoServiceImpl extends ServiceImpl<CityInfoMapper, CityInfo>
    implements CityInfoService {

    /**
     * 根据省份ID查询城市列表（带缓存）
     * 缓存1小时
     */
    @Cacheable(key = CacheConstant.REGION_CITIES_KEY + ":#provinceId")
    public List<CityInfo> listByProvinceId(Long provinceId) {
        LambdaQueryWrapper<CityInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CityInfo::getProvinceId, provinceId);
        return baseMapper.selectList(queryWrapper);
    }
    
    /**
     * 更新时清除缓存
     */
    @Override
    @CacheEvict(allEntries = true)
    public boolean updateById(CityInfo entity) {
        return super.updateById(entity);
    }
    
    /**
     * 保存时清除缓存
     */
    @Override
    @CacheEvict(allEntries = true)
    public boolean save(CityInfo entity) {
        return super.save(entity);
    }
}




