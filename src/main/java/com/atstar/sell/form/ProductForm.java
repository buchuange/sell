package com.atstar.sell.form;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: Dawn
 * @Date: 2022/4/21 17:25
 */
@Data
public class ProductForm {

    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品单价
     */
    private BigDecimal productPrice;

    /**
     * 商品库存
     */
    private Integer productStock;

    /**
     * 商品描述
     */
    private String productDescription;

    /**
     * 商品小图
     */
    private String productIcon;

    /**
     * 类目编号
     */
    private Integer categoryType;
}
