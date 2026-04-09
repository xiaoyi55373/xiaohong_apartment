package com.xiaohong.lease.web.admin.mapper;

import com.xiaohong.lease.model.entity.LeaseAgreement;
import com.xiaohong.lease.model.enums.LeaseStatus;
import com.xiaohong.lease.web.admin.vo.agreement.AgreementQueryVo;
import com.xiaohong.lease.web.admin.vo.agreement.AgreementVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
* @author 小红
* @description 针对表【lease_agreement(租约信息表)】的数据库操作Mapper
* @createDate 2023-07-24 15:48:00
* @Entity com.xiaohong.lease.model.LeaseAgreement
*/
public interface LeaseAgreementMapper extends BaseMapper<LeaseAgreement> {

    IPage<AgreementVo> pageItem(IPage<AgreementVo> page, AgreementQueryVo queryVo);

    /**
     * 查询即将到期的租约
     * @param targetDate 目标日期
     * @param statuses 租约状态列表
     * @return 租约列表
     */
    List<LeaseAgreement> selectExpiringLeases(@Param("targetDate") Date targetDate, 
                                               @Param("statuses") List<LeaseStatus> statuses);
}
