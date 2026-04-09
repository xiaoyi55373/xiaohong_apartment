package com.xiaohong.lease.web.app.service;

import com.xiaohong.lease.model.entity.PaymentType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 小红
* @description 针对表【payment_type(支付方式表)】的数据库操作Service
* @createDate 2023-07-26 11:12:39
*/
public interface PaymentTypeService extends IService<PaymentType> {

    List<PaymentType> listPaymentTypeByRoomId(Long id);
}
