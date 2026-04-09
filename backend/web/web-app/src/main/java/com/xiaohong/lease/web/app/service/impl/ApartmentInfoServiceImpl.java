package com.xiaohong.lease.web.app.service.impl;

import com.xiaohong.lease.common.cache.CacheConstant;
import com.xiaohong.lease.model.entity.ApartmentInfo;
import com.xiaohong.lease.model.entity.FacilityInfo;
import com.xiaohong.lease.model.entity.LabelInfo;
import com.xiaohong.lease.model.enums.ItemType;
import com.xiaohong.lease.web.app.mapper.*;
import com.xiaohong.lease.web.app.service.ApartmentInfoService;
import com.xiaohong.lease.web.app.vo.apartment.ApartmentDetailVo;
import com.xiaohong.lease.web.app.vo.graph.GraphVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaohong.lease.web.app.mapper.ApartmentInfoMapper;
import com.xiaohong.lease.web.app.mapper.FacilityInfoMapper;
import com.xiaohong.lease.web.app.mapper.GraphInfoMapper;
import com.xiaohong.lease.web.app.mapper.LabelInfoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 小红
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;


    /**
     * 获取公寓详情
     * 使用缓存，缓存30分钟
     */
    @Override
    @Cacheable(value = "apartment", key = "'" + CacheConstant.APARTMENT_DETAIL_KEY + ":' + #id", unless = "#result == null")
    public ApartmentDetailVo getDetailById(Long id) {

        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectApartmentById(id);
        if (apartmentInfo == null) {
            return null;
        }

        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, id);

        List<LabelInfo> labelInfoList = labelInfoMapper.selectListByApartmentId(id);

        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListByApartmentId(id);

        BigDecimal minRent = apartmentInfoMapper.selectMinRentByApartmentId(id);


        ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();

        BeanUtils.copyProperties(apartmentInfo, apartmentDetailVo);
        apartmentDetailVo.setIsDelete(apartmentInfo.getIsDeleted() == 1);
        apartmentDetailVo.setGraphVoList(graphVoList);
        apartmentDetailVo.setLabelInfoList(labelInfoList);
        apartmentDetailVo.setFacilityInfoList(facilityInfoList);
        apartmentDetailVo.setMinRent(minRent);
        return apartmentDetailVo;
    }
    
    /**
     * 更新公寓信息时清除缓存
     */
    @CacheEvict(value = "apartment", key = "'" + CacheConstant.APARTMENT_DETAIL_KEY + ":' + #entity.id")
    @Override
    public boolean updateById(ApartmentInfo entity) {
        return super.updateById(entity);
    }
    
    /**
     * 删除公寓时清除缓存
     */
    @CacheEvict(value = "apartment", allEntries = true)
    @Override
    public boolean removeById(java.io.Serializable id) {
        return super.removeById(id);
    }
}




