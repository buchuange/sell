package com.atstar.sell.repository;

import com.atstar.sell.domain.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author: Dawn
 * @Date: 2022/4/12 13:42
 */
public interface ProductInfoRepository extends JpaRepository<ProductInfo, Long> {

    List<ProductInfo> findByProductStatus(Integer productStatus);

    List<ProductInfo> findByProductIdIn(List<Long> productIdList);
}
