package com.xiaohong.lease.web.admin.service.impl;

import com.xiaohong.lease.web.admin.mapper.*;
import com.xiaohong.lease.web.admin.service.LeaseAgreementService;
import com.xiaohong.lease.web.admin.vo.agreement.AgreementQueryVo;
import com.xiaohong.lease.web.admin.vo.agreement.AgreementVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaohong.lease.model.entity.*;
import com.xiaohong.lease.web.admin.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 小红
 * @description 针对表【lease_agreement(租约信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement>
        implements LeaseAgreementService {

    @Autowired
    private LeaseAgreementMapper leaseAgreementMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired

    private RoomInfoMapper roomInfoMapper;
    @Autowired

    private PaymentTypeMapper paymentTypeMapper;

    @Autowired
    private LeaseTermMapper leaseTermMapper;

    @Override
    public IPage<AgreementVo> pageItem(IPage<AgreementVo> page, AgreementQueryVo queryVo) {

        return leaseAgreementMapper.pageItem(page,queryVo);
    }

    @Override
    public AgreementVo getAgreementById(Long id) {

        LeaseAgreement leaseAgreement = leaseAgreementMapper.selectById(id);

        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectApartmentById(leaseAgreement.getApartmentId());

        RoomInfo roomInfo = roomInfoMapper.selectRoomById(leaseAgreement.getRoomId());

        PaymentType paymentType = paymentTypeMapper.selectPaymentById(leaseAgreement.getPaymentTypeId());

        LeaseTerm leaseTerm = leaseTermMapper.selectTermById(leaseAgreement.getLeaseTermId());

        AgreementVo adminAgreementVo = new AgreementVo();
        BeanUtils.copyProperties(leaseAgreement, adminAgreementVo);

        adminAgreementVo.setApartmentInfo(apartmentInfo);
        adminAgreementVo.setRoomInfo(roomInfo);
        adminAgreementVo.setPaymentType(paymentType);
        adminAgreementVo.setLeaseTerm(leaseTerm);

        return adminAgreementVo;

    }

    @Override
    public List<AgreementVo> listAll(AgreementQueryVo queryVo) {
        // 使用分页查询所有数据（设置一个较大的页面大小）
        Page<AgreementVo> page = new Page<>(1, 10000);
        IPage<AgreementVo> result = leaseAgreementMapper.pageItem(page, queryVo);
        return result.getRecords();
    }
}




