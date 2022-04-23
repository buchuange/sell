package com.atstar.sell.service;

import com.atstar.sell.domain.ProductInfo;
import com.atstar.sell.dto.CartDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    ProductInfo findOne(Long productId);

    /**
     * 查询所有在架商品列表
     * @return
     */
    List<ProductInfo> findUpAll();

    /**
     * 查询所有下架商品列表
     * @return
     */
    List<ProductInfo> findDownAll();

    Page<ProductInfo> findAll(Pageable pageable);

    ProductInfo save(ProductInfo productInfo);

    List<ProductInfo> findByProductIdIn(List<Long> productIdList);

    //TODO 加库存
    void increaseStock(List<CartDTO> cartDTOList);

    //TODO 减库存
    void decreaseStock(List<CartDTO> cartDTOList);

    // 上架
    ProductInfo onSale(Long productId);

    // 下架
    ProductInfo offSale(Long productId);
}
