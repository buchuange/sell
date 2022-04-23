package com.atstar.sell.domain;

import com.atstar.sell.enums.OrderStatusEnum;
import com.atstar.sell.enums.PayStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: Dawn
 * @Date: 2022/4/13 01:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicUpdate
public class OrderMaster {

    /**
     * 订单Id
     */
    @Id
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
    private Integer orderStatus = OrderStatusEnum.NEW.getCode();

    /**
     * 支付状态, 默认为未支付
     */
    private Integer payStatus = PayStatusEnum.WAIT_PAID.getCode();

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *  更新时间
     */
    private Date updateTime;


}
