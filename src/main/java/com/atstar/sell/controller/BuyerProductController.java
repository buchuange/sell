package com.atstar.sell.controller;

import com.atstar.sell.VO.ProductInfoVO;
import com.atstar.sell.VO.ProductVO;
import com.atstar.sell.VO.ResultVO;
import com.atstar.sell.domain.ProductCategory;
import com.atstar.sell.domain.ProductInfo;
import com.atstar.sell.service.CategoryService;
import com.atstar.sell.service.ProductService;
import com.atstar.sell.utils.ResultVOUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Dawn
 * @Date: 2022/4/12 17:20
 */
@RestController
@RequestMapping("/buyer/product")
public class BuyerProductController {

    @Resource
    private ProductService productService;

    @Resource
    private CategoryService categoryService;

    @GetMapping("/list")
    // @Cacheable(cacheNames = "product", key = "#sellerId", condition="#sellerId.length() > 3", unless = "#result.getCode() != 0")
    public ResultVO<List<ProductVO>> list() {

        // 1、查询所有上架商品
        List<ProductInfo> productInfoList = productService.findUpAll();

        // 2、查询类目（一次性查询）
        List<Integer> categoryTypeList = productInfoList.stream()
                .map(ProductInfo::getCategoryType)
                .collect(Collectors.toList());
        List<ProductCategory> productCategoryList = categoryService.findByCategoryTypeIn(categoryTypeList);

        // 3、数据拼装
        List<ProductVO> productVOList = new ArrayList<>();

        for (ProductCategory category : productCategoryList) {

            ProductVO productVO = new ProductVO();
            productVO.setCategoryName(category.getCategoryName());
            productVO.setCategoryType(category.getCategoryType());

            List<ProductInfoVO> productInfoVOList = new ArrayList<>();
            for (ProductInfo productInfo : productInfoList) {
                if (productInfo.getCategoryType().equals(category.getCategoryType())) {

                    ProductInfoVO productInfoVO = new ProductInfoVO();
                    BeanUtils.copyProperties(productInfo, productInfoVO);
                    productInfoVOList.add(productInfoVO);
                }
            }

            productVO.setProductInfoVOList(productInfoVOList);
            productVOList.add(productVO);
        }

        return ResultVOUtil.success(productVOList);
    }
}
