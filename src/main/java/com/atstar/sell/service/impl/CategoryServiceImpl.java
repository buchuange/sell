package com.atstar.sell.service.impl;

import com.atstar.sell.domain.ProductCategory;
import com.atstar.sell.repository.ProductCategoryRepository;
import com.atstar.sell.service.CategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: Dawn
 * @Date: 2022/4/12 01:57
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private ProductCategoryRepository repository;

    @Override
    public ProductCategory findOne(Integer categoryId) {
        return repository.findById(categoryId).orElse(null);
    }

    @Override
    public List<ProductCategory> findAll() {
        return repository.findAll();
    }

    @Override
    public List<ProductCategory> findByCategoryTypeIn(List<Integer> categoryTypeList) {
        return repository.findByCategoryTypeIn(categoryTypeList);
    }

    @Override
    public ProductCategory save(ProductCategory productCategory) {
        return repository.save(productCategory);
    }
}
