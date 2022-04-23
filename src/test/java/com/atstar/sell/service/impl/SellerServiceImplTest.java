package com.atstar.sell.service.impl;

import com.atstar.sell.SellApplicationTests;
import com.atstar.sell.domain.SellerInfo;
import com.atstar.sell.service.SellerService;
import com.atstar.sell.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class SellerServiceImplTest extends SellApplicationTests {

    @Resource
    private SellerService sellerService;

    @Test
    public void findByOpenid() {

        SellerInfo result = sellerService.findByOpenid("oVUEp6xV9dYpw51HyQBj5VbUT44Q");

        log.info(JsonUtil.print(result));

        Assertions.assertNotNull(result);
    }
}