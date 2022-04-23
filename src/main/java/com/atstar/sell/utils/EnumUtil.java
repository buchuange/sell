package com.atstar.sell.utils;

import com.atstar.sell.enums.CodeEnum;

/**
 * @Author: Dawn
 * @Date: 2022/4/20 00:55
 */
public class EnumUtil {

    public static <T extends CodeEnum> T getByCode(Integer code, Class<T> enumClass) {

        for (T each: enumClass.getEnumConstants()) {

            if (code.equals(each.getCode())) {
                return each;
            }
        }
        return null;
    }
}
