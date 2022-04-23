package com.atstar.sell.service.impl;

import com.atstar.sell.SellApplicationTests;
import com.atstar.sell.dto.OrderDTO;
import com.atstar.sell.service.OrderService;
import com.atstar.sell.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

@Slf4j
public class PayServiceImplTest extends SellApplicationTests {

    @Resource
    private PayService payService;

    @Resource
    private OrderService orderService;

    @Test
    public void create() {

        OrderDTO orderDTO = orderService.findByOrderId(1650273200442L);
        payService.create(orderDTO);
    }

    @Test
    public void refund() {

        OrderDTO orderDTO = orderService.findByOrderId(1650273200442L);
        payService.refund(orderDTO);
    }
}