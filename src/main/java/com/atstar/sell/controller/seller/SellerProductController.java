package com.atstar.sell.controller.seller;

import com.atstar.sell.domain.ProductCategory;
import com.atstar.sell.domain.ProductInfo;
import com.atstar.sell.dto.OrderDTO;
import com.atstar.sell.enums.ResultEnum;
import com.atstar.sell.exception.SellException;
import com.atstar.sell.form.ProductForm;
import com.atstar.sell.service.CategoryService;
import com.atstar.sell.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author: Dawn
 * @Date: 2022/4/20 23:52
 */
@Slf4j
@Controller
@RequestMapping("/seller/product")
public class SellerProductController {

    @Resource
    private ProductService productService;

    @Resource
    private CategoryService categoryService;

    /**
     * 查询商品列表
     *
     * @param page 第几页 从1开始
     * @param size 每页几条数据
     * @return
     */
    @GetMapping("/list")
    public String list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "size", defaultValue = "10") Integer size, Model model) {

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("createTime").descending());

        Page<ProductInfo> productInfoPage = productService.findAll(pageRequest);

        model.addAttribute("productInfoPage", productInfoPage);
        model.addAttribute("currentPage", page);

        return "product/list";
    }

    @GetMapping("/on_sale/{productId}")
    public String on_sale(@PathVariable("productId") Long productId, Model model) {

        try {

            productService.onSale(productId);

        } catch (SellException e) {
            log.error("【卖家上架商品】发生异常，msg={}", e.getMsg());

            model.addAttribute("msg", e.getMsg());
            model.addAttribute("url", "/seller/product/list");

            return "common/error";
        }

        model.addAttribute("msg", ResultEnum.PRODUCT_ON_SALE_SUCCESS.getMsg());
        model.addAttribute("url", "/seller/product/list");

        return "common/success";
    }

    @GetMapping("/off_sale/{productId}")
    public String off_sale(@PathVariable("productId") Long productId, Model model) {

        try {

            productService.offSale(productId);

        } catch (SellException e) {
            log.error("【卖家下架商品】发生异常，msg={}", e.getMsg());

            model.addAttribute("msg", e.getMsg());
            model.addAttribute("url", "/seller/product/list");

            return "common/error";
        }

        model.addAttribute("msg", ResultEnum.PRODUCT_OFF_SALE_SUCCESS.getMsg());
        model.addAttribute("url", "/seller/product/list");

        return "common/success";
    }

    @GetMapping("/index")
    public String index(@RequestParam(value = "productId", required = false) Long productId, Model model) {

        ProductInfo productInfo = new ProductInfo();

        if (!ObjectUtils.isEmpty(productId)) {
            productInfo = productService.findOne(productId);
        }

        model.addAttribute("productInfo", productInfo);

        // 查询所有类目
        List<ProductCategory> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);


        return "product/index";
    }

    @PostMapping("/save")
    // @CachePut(cacheNames = "product", key = "123")
    // @CacheEvict(cacheNames = "product", key = "123")
    public String save(@Valid ProductForm productForm, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("msg", bindingResult.getFieldError().getDefaultMessage());
            model.addAttribute("url", "/seller/product/index");
        }

        ProductInfo productInfo = new ProductInfo();

        try {

            if (!ObjectUtils.isEmpty(productForm.getProductId())) {
                productInfo = productService.findOne(productForm.getProductId());
            }

            BeanUtils.copyProperties(productForm, productInfo);
            productService.save(productInfo);

        } catch (SellException e) {

            model.addAttribute("msg", e.getMsg());
            model.addAttribute("url", "/seller/product/index");

            return "common/error";
        }

        model.addAttribute("msg", ResultEnum.SUCCESS.getMsg());
        model.addAttribute("url", "/seller/product/list");

        return "common/success";
    }
}
