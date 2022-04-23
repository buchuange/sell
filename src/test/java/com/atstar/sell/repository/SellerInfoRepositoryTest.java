package com.atstar.sell.repository;

import com.atstar.sell.SellApplicationTests;
import com.atstar.sell.domain.SellerInfo;
import com.atstar.sell.utils.JsonUtil;
import com.atstar.sell.utils.KeyUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class SellerInfoRepositoryTest extends SellApplicationTests {

    @Resource
    private SellerInfoRepository repository;


    @Test
    public void save() {

        SellerInfo sellerInfo = new SellerInfo();

        sellerInfo.setSellerId(KeyUtil.generateUniqueKey());
        sellerInfo.setUsername("星辰大帝");
        sellerInfo.setPassword("123456");
        sellerInfo.setOpenid("oVUEp6xV9dYpw51HyQBj5VbUT44Q");

        SellerInfo result = repository.save(sellerInfo);

        log.info(JsonUtil.print(result));

        Assertions.assertNotNull(result);
    }
    @Test
    public void findByOpenid() {

        SellerInfo result = repository.findByOpenid("oVUEp6xV9dYpw51HyQBj5VbUT44Q");

        log.info(JsonUtil.print(result));

        Assertions.assertNotNull(result);
    }
}