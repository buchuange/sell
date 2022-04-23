package com.atstar.sell.service.impl;

import com.atstar.sell.dto.OrderDTO;
import com.atstar.sell.enums.ResultEnum;
import com.atstar.sell.exception.SellException;
import com.atstar.sell.service.OrderService;
import com.atstar.sell.service.PayService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.model.RefundRequest;
import com.lly835.bestpay.model.RefundResponse;
import com.lly835.bestpay.service.BestPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @Author: Dawn
 * @Date: 2022/4/18 19:37
 */
@Slf4j
@Service
public class PayServiceImpl implements PayService {

    @Resource
    private BestPayService bestPayService;

    @Resource
    private OrderService orderService;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public PayResponse create(OrderDTO orderDTO) {

        PayRequest payRequest = new PayRequest();
        payRequest.setPayTypeEnum(BestPayTypeEnum.WXPAY_MP);
        payRequest.setOpenid(orderDTO.getBuyerOpenid());
        payRequest.setOrderId(String.valueOf(orderDTO.getOrderId()));
        payRequest.setOrderAmount(orderDTO.getOrderAmount().doubleValue());
        payRequest.setOrderName("微信点餐订单");

        log.info("【微信支付】发起支付，payRequest={}", gson.toJson(payRequest));

        PayResponse payResponse = bestPayService.pay(payRequest);

        log.info("【微信支付】发起支付，payResponse={}", gson.toJson(payResponse));

        return payResponse;
    }

    @Override
    @Transactional
    public void asyncNotify(String notifyData) {

        // 1、签名校验
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("【微信支付】异步通知 payResponse: {}", gson.toJson(payResponse));

        // 2、金额校验（从数据库查订单）
        OrderDTO orderDTO = orderService.findByOrderId(Long.valueOf(payResponse.getOrderId()));

        if (orderDTO.getOrderAmount().compareTo(BigDecimal.valueOf(payResponse.getOrderAmount())) != 0) {
            log.info("【微信支付】异步通知，金额校验失败，orderId={}，微信通知金额={}，数据库金额={}",
                    payResponse.getOrderId(), payResponse.getOrderAmount(), orderDTO.getOrderAmount());
            throw new SellException(ResultEnum.WECHAT_NOTIFY_MONEY_VERIFY_ERROR, "异步通知的金额和数据库中保存的金额不一致，orderId=" + payResponse.getOrderId());
        }

        // 3、修改订单支付状态
        orderService.paid(Long.valueOf(payResponse.getOrderId()));
    }

    @Override
    public void refund(OrderDTO orderDTO) {

        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setOrderId(String.valueOf(orderDTO.getOrderId()));
        refundRequest.setOrderAmount(orderDTO.getOrderAmount().doubleValue());
        refundRequest.setPayTypeEnum(BestPayTypeEnum.WXPAY_MP);

        log.info("【微信退款】refundRequest={}", gson.toJson(refundRequest));

        RefundResponse refundResponse = bestPayService.refund(refundRequest);

        log.info("【微信退款】refundResponse={}", gson.toJson(refundResponse));

    }
}
