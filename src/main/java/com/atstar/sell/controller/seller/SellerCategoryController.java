package com.atstar.sell.controller.seller;

import com.atstar.sell.domain.ProductCategory;
import com.atstar.sell.domain.ProductInfo;
import com.atstar.sell.enums.ResultEnum;
import com.atstar.sell.exception.SellException;
import com.atstar.sell.form.CategoryForm;
import com.atstar.sell.form.ProductForm;
import com.atstar.sell.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author: Dawn
 * @Date: 2022/4/21 19:26
 */
@Slf4j
@Controller
@RequestMapping("/seller/category")
public class SellerCategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/list")
    public String list(Model model) {

        List<ProductCategory> categoryList = categoryService.findAll();

        model.addAttribute("categoryList", categoryList);

        return "category/list";
    }

    @GetMapping("/index")
    public String index(@RequestParam(value = "categoryId", required = false) Integer categoryId, Model model) {

        ProductCategory category = new ProductCategory();

        if (!ObjectUtils.isEmpty(categoryId)) {
            category = categoryService.findOne(categoryId);
        }

        model.addAttribute("category", category);


        return "category/index";
    }

    @PostMapping("/save")
    public String save(@Valid CategoryForm categoryForm, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("msg", bindingResult.getFieldError().getDefaultMessage());
            model.addAttribute("url", "/seller/category/index");
        }

        ProductCategory category = new ProductCategory();

        try {

            if (!ObjectUtils.isEmpty(categoryForm.getCategoryId())) {
                category = categoryService.findOne(categoryForm.getCategoryId());
            }

            BeanUtils.copyProperties(categoryForm, category);
            categoryService.save(category);

        } catch (Throwable e) {

            model.addAttribute("msg", e.getMessage());
            model.addAttribute("url", "/seller/category/index");

            return "common/error";
        }

        model.addAttribute("msg", "");
        model.addAttribute("url", "/seller/category/list");

        return "common/success";
    }
}
