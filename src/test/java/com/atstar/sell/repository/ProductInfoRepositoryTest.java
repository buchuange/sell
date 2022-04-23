package com.atstar.sell.repository;

import com.atstar.sell.SellApplicationTests;
import com.atstar.sell.domain.ProductInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
public class ProductInfoRepositoryTest extends SellApplicationTests {

    @Resource
    private ProductInfoRepository repository;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    @Test
    public void save() {
        ProductInfo productInfo = new ProductInfo();
        productInfo.setProductName("炸鸡套餐");
        productInfo.setProductPrice(new BigDecimal("19.8"));
        productInfo.setProductStock(200);
        productInfo.setProductDescription("美味炸鸡套餐，包含可乐+炸鸡");
        productInfo.setProductIcon("http://xxx.jpg");
        productInfo.setProductStatus(0);
        productInfo.setCategoryType(6);

        ProductInfo result = repository.save(productInfo);

        log.info(gson.toJson(result));

        Assertions.assertNotNull(result);
    }

    @Test
    public void findByProductStatus() {

        List<ProductInfo> productInfos = repository.findByProductStatus(0);

        log.info(gson.toJson(productInfos));

        Assertions.assertNotEquals(0, productInfos.size());


    }
}