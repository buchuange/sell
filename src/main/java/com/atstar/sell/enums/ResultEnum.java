package com.atstar.sell.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Dawn
 * @Date: 2022/4/12 18:10
 */
@Getter
@AllArgsConstructor
public enum ResultEnum {

    ERROR(-1, "服务端错误"),

    SUCCESS(0, "成功"),

    PARAM_ERROR(2, "传入参数错误"),

    LOGIN_FAIL(4, "登录失败，登录信息不匹配"),

    LOGOUT_SUCCESS(5, "登出成功!"),

    LOGOUT_FAIL(6, "登出失败，请先登录！"),

    USER_NOT_LOGIN(7, "用户未登录"),

    PRODUCT_NOT_EXIST(10, "商品不存在"),

    NO_ENOUGH_STOCK(11, "商品库存不足"),

    PRODUCT_STATUS_ERROR(12, "商品状态不正确"),

    PRODUCT_ON_SALE_SUCCESS(13, "商品上架成功"),

    PRODUCT_OFF_SALE_SUCCESS(14, "商品下架成功"),

    ORDER_NOT_EXIST(20, "订单不存在"),

    ORDER_DETAIL_NOT_EXIST(21, "订单详情不存在"),

    ORDER_STATUS_ERROR(22, "订单状态不正确"),

    ORDER_UPDATE_FAIL(23, "订单更新失败"),

    ORDER_DETAIL_EMPTY(24, "订单详情为空"),

    ORDER_PAY_STATUS_ERROR(25, "订单支付状态不正确"),

    ORDER_OPERATION_ERROR(26, "订单操作异常"),

    ORDER_CANCEL_SUCCESS(27, "订单取消成功"),

    ORDER_FINISH_SUCCESS(28, "订单完结成功"),

    CART_EMPTY(29, "购物车不能为空"),

    WECHAT_MP_ERROR(30, "微信公众账号方面错误"),

    WECHAT_NOTIFY_MONEY_VERIFY_ERROR(31, "微信支付异步通知金额交易不通过"),

    SEC_KILL_END(40, "秒杀活动结束"),

    SEC_KILL_FAIL(41, "秒杀商品失败");;


    private final Integer code;

    private final String msg;
}
