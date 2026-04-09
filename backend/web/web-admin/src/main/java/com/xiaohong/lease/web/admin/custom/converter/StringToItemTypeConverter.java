package com.xiaohong.lease.web.admin.custom.converter;

import com.xiaohong.lease.model.enums.ItemType;
import org.springframework.core.convert.converter.Converter;

//无实际作用
public class StringToItemTypeConverter implements Converter<String, ItemType> {

    @Override
    public ItemType convert(String code) {

        ItemType[] enumConstants = ItemType.class.getEnumConstants();
        for (ItemType enumConstant : enumConstants) {
            if (enumConstant.getCode().equals(Integer.valueOf(code))) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("参数非法");
    }
}
