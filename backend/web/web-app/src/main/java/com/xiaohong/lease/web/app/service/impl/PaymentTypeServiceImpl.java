package com.xiaohong.lease.web.app.service.impl;

import com.xiaohong.lease.common.cache.CacheConstant;
import com.xiaohong.lease.model.entity.PaymentType;
import com.xiaohong.lease.web.app.mapper.PaymentTypeMapper;
import com.xiaohong.lease.web.app.service.PaymentTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 小红
* @description 针对表【payment_type(支付方式表)】的数据库操作Service实现
* @createDate 2023-07-26 11:12:39
*/
@Service
@CacheConfig(cacheNames = "config")
public class PaymentTypeServiceImpl extends ServiceImpl<PaymentTypeMapper, PaymentType>
    implements PaymentTypeService {

    @Autowired
    private PaymentTypeMapper paymentTypeMapper;

    /**
     * 根据房间ID查询支付方式（带缓存）
     */
    @Override
    @Cacheable(key = CacheConstant.PAYMENT_TYPES_KEY + ":#id")
    public List<PaymentType> listPaymentTypeByRoomId(Long id) {
        return paymentTypeMapper.selectListByRoomId(id);
    }
    
    /**
     * 获取所有支付方式列表（带缓存）
     * 缓存2小时
     */
    @Override
    @Cacheable(key = "'" + CacheConstant.PAYMENT_TYPES_KEY + "'")
    public List<PaymentType> list() {
        return super.list();
    }
    
    /**
     * 更新时清除缓存
     */
    @Override
    @CacheEvict(allEntries = true)
    public boolean updateById(PaymentType entity) {
        return super.updateById(entity);
    }
    
    /**
     * 保存时清除缓存
     */
    @Override
    @CacheEvict(allEntries = true)
    public boolean save(PaymentType entity) {
        return super.save(entity);
    }
}




