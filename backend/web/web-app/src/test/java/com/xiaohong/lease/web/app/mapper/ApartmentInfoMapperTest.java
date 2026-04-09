package com.xiaohong.lease.web.app.mapper;

import com.xiaohong.lease.web.app.vo.apartment.ApartmentItemVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApartmentInfoMapperTest {

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Test
    void selectApartmentById() {
        ApartmentItemVo apartmentItemVo = apartmentInfoMapper.selectApartmentItemVoById(9L);
        System.out.println();
    }
}