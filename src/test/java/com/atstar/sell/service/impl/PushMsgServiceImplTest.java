package com.atstar.sell.service.impl;

import com.atstar.sell.SellApplicationTests;
import com.atstar.sell.dto.OrderDTO;
import com.atstar.sell.service.OrderService;
import com.atstar.sell.service.PushMsgService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class PushMsgServiceImplTest extends SellApplicationTests {

    @Resource
    private PushMsgService pushMsgService;

    @Resource
    private OrderService orderService;

    @Test
    public void pushOrderStatus() {

        OrderDTO orderDTO = orderService.findByOrderId(1650041053037L);

        pushMsgService.pushOrderStatus(orderDTO);

    }
}