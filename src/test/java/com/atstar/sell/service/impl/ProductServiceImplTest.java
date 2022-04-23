package com.atstar.sell.service.impl;

import com.atstar.sell.SellApplicationTests;
import com.atstar.sell.domain.ProductInfo;
import com.atstar.sell.enums.ProductStatusEnum;
import com.atstar.sell.service.ProductService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
public class ProductServiceImplTest extends SellApplicationTests {

    @Resource
    private ProductService productService;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void findOne() {

        ProductInfo productInfo = productService.findOne(1000001L);
        log.info(gson.toJson(productInfo));

        Assertions.assertEquals(1000001L, productInfo.getProductId());
    }

    @Test
    public void findUpAll() {

        List<ProductInfo> productInfos = productService.findUpAll();

        log.info(gson.toJson(productInfos));

        Assertions.assertNotEquals(0, productInfos.size());
    }

    @Test
    public void findDownAll() {

        List<ProductInfo> productInfos = productService.findDownAll();

        log.info(gson.toJson(productInfos));

        Assertions.assertNotEquals(0, productInfos.size());
    }

    @Test
    public void findAll() {

        PageRequest page = PageRequest.of(0, 2, Sort.by("productId").descending());

        Page<ProductInfo> productInfos = productService.findAll(page);

        log.info(gson.toJson(productInfos));

        Assertions.assertNotEquals(0, productInfos.getTotalPages());
    }

    @Test
    public void save() {

        ProductInfo productInfo = new ProductInfo();
        productInfo.setProductName("琥珀烤奶");
        productInfo.setProductPrice(new BigDecimal("7.5"));
        productInfo.setProductStock(100);
        productInfo.setProductDescription("琥珀烤奶，夏日饮品");
        productInfo.setProductIcon("http://xxx.jpg");
        productInfo.setProductStatus(ProductStatusEnum.UP.getCode());
        productInfo.setCategoryType(2);

        ProductInfo result = productService.save(productInfo);

        log.info(gson.toJson(result));

        Assertions.assertNotNull(result);
    }
}