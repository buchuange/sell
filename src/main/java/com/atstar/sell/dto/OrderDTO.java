package com.atstar.sell.dto;

import com.atstar.sell.domain.OrderDetail;
import com.atstar.sell.enums.CodeEnum;
import com.atstar.sell.enums.OrderStatusEnum;
import com.atstar.sell.enums.PayStatusEnum;
import com.atstar.sell.utils.EnumUtil;
import com.atstar.sell.utils.serializer.Date2LongSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author: Dawn
 * @Date: 2022/4/14 01:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
// @JsonInclude(value = JsonInclude.Include.NON_NULL) 使用配置文件配置 全局生效
public class OrderDTO {

    /**
     * 订单Id
     */
    private Long orderId;

    /**
     * 买家名字
     */
    private String buyerName;

    /**
     * 买家手机号
     */
    private String buyerPhone;

    /**
     * 买家地址
     */
    private String buyerAddress;

    /**
     * 买家微信OpenId
     */
    private String buyerOpenid;

    /**
     *  订单总金额
     */
    private BigDecimal orderAmount;

    /**
     *  订单状态，默认为新订单
     */
    private Integer orderStatus;

    /**
     * 支付状态, 默认为未支付
     */
    private Integer payStatus;

    /**
     * 创建时间
     */
    @JsonSerialize(using = Date2LongSerializer.class)
    private Date createTime;

    /**
     *  更新时间
     */
    @JsonSerialize(using = Date2LongSerializer.class)
    private Date updateTime;

    private List<OrderDetail> orderDetailList;

    @JsonIgnore
    public OrderStatusEnum getOrderStatusEnum() {

        return EnumUtil.getByCode(orderStatus, OrderStatusEnum.class);
    }

    @JsonIgnore
    public PayStatusEnum getPayStatusEnum() {

        return EnumUtil.getByCode(payStatus, PayStatusEnum.class);
    }

}
