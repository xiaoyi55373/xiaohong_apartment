package com.xiaohong.lease.web.admin.service;

import com.xiaohong.lease.model.entity.LeaseAgreement;
import com.xiaohong.lease.web.admin.vo.agreement.AgreementQueryVo;
import com.xiaohong.lease.web.admin.vo.agreement.AgreementVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 小红
* @description 针对表【lease_agreement(租约信息表)】的数据库操作Service
* @createDate 2023-07-24 15:48:00
*/
public interface LeaseAgreementService extends IService<LeaseAgreement> {

    IPage<AgreementVo> pageItem(IPage<AgreementVo> page, AgreementQueryVo queryVo);

    AgreementVo getAgreementById(Long id);

    /**
     * 查询所有租约信息（用于导出）
     * @param queryVo 查询条件
     * @return 租约列表
     */
    List<AgreementVo> listAll(AgreementQueryVo queryVo);
}
