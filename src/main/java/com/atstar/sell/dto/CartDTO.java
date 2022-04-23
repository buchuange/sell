package com.atstar.sell.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Dawn
 * @Date: 2022/4/14 21:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品数量
     */
    private Integer productQuantity;
}
