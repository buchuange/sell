package com.atstar.sell.repository;

import com.atstar.sell.SellApplicationTests;
import com.atstar.sell.domain.OrderMaster;
import com.atstar.sell.utils.KeyUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class OrderMasterRepositoryTest extends SellApplicationTests {

    @Resource
    public OrderMasterRepository repository;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void save() {

        OrderMaster orderMaster = new OrderMaster();
        orderMaster.setOrderId(KeyUtil.generateUniqueKey());
        orderMaster.setBuyerName("King");
        orderMaster.setBuyerPhone("15645641221");
        orderMaster.setBuyerAddress("光城里");
        orderMaster.setBuyerOpenid("1111");
        orderMaster.setOrderAmount(new BigDecimal("15.6"));

        OrderMaster result = repository.save(orderMaster);
        log.info(gson.toJson(result));

        Assertions.assertNotNull(result);
    }

    @Test
    public void findByBuyerOpenid() {

        PageRequest page = PageRequest.of(0, 2);

        Page<OrderMaster> orderMasters = repository.findByBuyerOpenid("1111", page);

        log.info(gson.toJson(orderMasters));

        Assertions.assertNotEquals(0, orderMasters.getTotalElements());
    }
}