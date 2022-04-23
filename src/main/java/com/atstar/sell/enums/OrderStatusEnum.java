package com.atstar.sell.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum implements CodeEnum {

    NEW(0, "新订单"),

    TRADE_SUCCESS(1, "订单完结"),

    CANCELED(2, "订单取消");

    private final Integer code;

    private final String msg;
}
