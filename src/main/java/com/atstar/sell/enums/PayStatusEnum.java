package com.atstar.sell.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayStatusEnum implements CodeEnum {

    WAIT_PAID(0, "等待支付"),

    PAID_SUCCESS(1, "支付成功");

    private final Integer code;

    private final String msg;
}
