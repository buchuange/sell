package com.atstar.sell.service.impl;

import com.atstar.sell.SellApplicationTests;
import com.atstar.sell.domain.ProductCategory;
import com.atstar.sell.service.CategoryService;
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
public class CategoryServiceImplTest extends SellApplicationTests {

    @Resource
    private CategoryService categoryService;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void findOne() {

        ProductCategory productCategory = categoryService.findOne(1);

        log.info(gson.toJson(productCategory));

        Assert.state(1 == productCategory.getCategoryId(), "测试失败------》查询记录失败");
    }

    @Test
    public void findAll() {

        List<ProductCategory> categoryList = categoryService.findAll();

        log.info(gson.toJson(categoryList));

        Assert.state(categoryList.size() != 0, "测试失败------》查询类目列表失败");

    }

    @Test
    public void findByCategoryTypeIn() {

        List<ProductCategory> categoryList = categoryService.findByCategoryTypeIn(Arrays.asList(1, 2, 3));

        log.info(gson.toJson(categoryList));

        Assert.state(categoryList.size() != 0, "测试失败------》查询类目列表失败");
    }

    @Test
    public void save() {

        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryName("啤酒饮料");
        productCategory.setCategoryType(6);

        ProductCategory result = categoryService.save(productCategory);

        log.info(gson.toJson(result));

        Assert.state(!ObjectUtils.isEmpty(result), "测试失败---》插入数据失败！");
    }
}