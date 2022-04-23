package com.atstar.sell.repository;

import com.atstar.sell.SellApplicationTests;
import com.atstar.sell.domain.OrderDetail;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class OrderDetailRepositoryTest extends SellApplicationTests {

    @Resource
    private OrderDetailRepository repository;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void save() {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(13130000000L);
        orderDetail.setProductId(1000002L);
        orderDetail.setProductName("琥珀烤奶");
        orderDetail.setProductPrice(new BigDecimal("45"));
        orderDetail.setProductIcon("http://xxx.jpg");
        orderDetail.setProductQuantity(6);

        OrderDetail result = repository.save(orderDetail);

        log.info(gson.toJson(result));

        Assertions.assertNotNull(result);
    }

    @Test
    public void findByOrderId() {

        List<OrderDetail> orderDetails = repository.findByOrderId(13130000000L);

        log.info(gson.toJson(orderDetails));

        Assertions.assertNotEquals(0, orderDetails.size());
    }
}