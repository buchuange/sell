package com.atstar.sell.repository;

import com.atstar.sell.SellApplicationTests;
import com.atstar.sell.domain.ProductCategory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


@Slf4j
public class ProductCategoryRepositoryTest extends SellApplicationTests {

    @Resource
    private ProductCategoryRepository productCategoryRepository;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void findOneTest() {

        ProductCategory productCategory = productCategoryRepository.findById(1).orElse(null);

        log.info(gson.toJson(productCategory));
    }

    @Test
    public void saveTest() {

        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryName("夜宵烧烤");
        productCategory.setCategoryType(3);

        ProductCategory result = productCategoryRepository.save(productCategory);

        Assert.state(!ObjectUtils.isEmpty(result), "测试失败---》插入数据失败！");
    }

    @Test
    public void updateTest() {

        ProductCategory productCategory = productCategoryRepository.findById(1).orElse(null);
        productCategory.setCategoryType(6);

        productCategoryRepository.save(productCategory);
    }

    @Test
    public void findByCategoryTypeInTest() {

        List<Integer> list = Arrays.asList(1, 2, 3, 4);

        List<ProductCategory> categoryTypes = productCategoryRepository.findByCategoryTypeIn(list);

        log.info(gson.toJson(categoryTypes));
    }
}