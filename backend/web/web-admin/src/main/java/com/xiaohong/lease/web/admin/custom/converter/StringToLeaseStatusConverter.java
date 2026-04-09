package com.xiaohong.lease.web.admin.custom.converter;

import com.xiaohong.lease.model.enums.LeaseStatus;
import org.springframework.core.convert.converter.Converter;

//无实际作用
public class StringToLeaseStatusConverter implements Converter<String, LeaseStatus> {
    @Override
    public LeaseStatus convert(String code) {

        LeaseStatus[] enumConstants = LeaseStatus.class.getEnumConstants();
        for (LeaseStatus enumConstant : enumConstants) {
            if (enumConstant.getCode().equals(Integer.valueOf(code))) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("参数非法");
    }
}
