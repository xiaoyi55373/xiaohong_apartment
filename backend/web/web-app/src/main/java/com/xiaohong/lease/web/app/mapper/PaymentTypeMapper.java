package com.xiaohong.lease.web.app.mapper;

import com.xiaohong.lease.model.entity.PaymentType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 小红
* @description 针对表【payment_type(支付方式表)】的数据库操作Mapper
* @createDate 2023-07-26 11:12:39
* @Entity com.xiaohong.lease.model.entity.PaymentType
*/
public interface PaymentTypeMapper extends BaseMapper<PaymentType> {

    List<PaymentType> selectListByRoomId(Long id);

    PaymentType selectPaymentTypeById(Long paymentTypeId);

}




