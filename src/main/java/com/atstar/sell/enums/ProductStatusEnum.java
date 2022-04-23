package com.atstar.sell.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Dawn
 * @Date: 2022/4/12 16:12
 */
@Getter
@AllArgsConstructor
public enum ProductStatusEnum {

    UP(0, "上架"),

    DOWN(1, "下架");

    private final Integer code;

    private final String msg;
}
