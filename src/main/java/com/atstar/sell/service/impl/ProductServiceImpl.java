package com.atstar.sell.service.impl;

import com.atstar.sell.domain.ProductInfo;
import com.atstar.sell.dto.CartDTO;
import com.atstar.sell.enums.ProductStatusEnum;
import com.atstar.sell.enums.ResultEnum;
import com.atstar.sell.exception.SellException;
import com.atstar.sell.repository.ProductInfoRepository;
import com.atstar.sell.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @Author: Dawn
 * @Date: 2022/4/12 16:10
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductInfoRepository repository;

    @Override
    public ProductInfo findOne(Long productId) {


        Optional<ProductInfo> productInfoOptional = repository.findById(productId);

        // 判断商品是否存在
        if (!productInfoOptional.isPresent()) {
            throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
        }

        return productInfoOptional.orElse(null);
    }

    @Override
    public List<ProductInfo> findUpAll() {
        return repository.findByProductStatus(ProductStatusEnum.UP.getCode());
    }

    @Override
    public List<ProductInfo> findDownAll() {

        return repository.findByProductStatus(ProductStatusEnum.DOWN.getCode());
    }

    @Override
    public Page<ProductInfo> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public ProductInfo save(ProductInfo productInfo) {
        return repository.save(productInfo);
    }

    @Override
    public List<ProductInfo> findByProductIdIn(List<Long> productIdList) {
        return repository.findByProductIdIn(productIdList);
    }

    @Override
    public void increaseStock(List<CartDTO> cartDTOList) {

        for (CartDTO cartDTO : cartDTOList) {

            ProductInfo productInfo = this.findOne(cartDTO.getProductId());

            //TODO 优化 Redis锁
            int result = productInfo.getProductStock() + cartDTO.getProductQuantity();
            productInfo.setProductStock(result);
            repository.save(productInfo);
        }
    }

    @Override
    public void decreaseStock(List<CartDTO> cartDTOList) {

        for (CartDTO cartDTO : cartDTOList) {
            ProductInfo productInfo = this.findOne(cartDTO.getProductId());

            //TODO 优化 Redis锁
            int result = productInfo.getProductStock() - cartDTO.getProductQuantity();

            // 判断库存是否充足
            if (result < 0) {
                throw new SellException(ResultEnum.NO_ENOUGH_STOCK);
            }

            productInfo.setProductStock(result);
            repository.save(productInfo);
        }
    }

    @Override
    public ProductInfo onSale(Long productId) {

        ProductInfo productInfo = this.findOne(productId);

        // 判断商品状态是否正确
        if (productInfo.getProductStatus().equals(ProductStatusEnum.UP.getCode())) {

            log.error("【上架商品】商品状态不正确，productId={}", productId);
            throw new SellException(ResultEnum.PRODUCT_STATUS_ERROR);
        }

        productInfo.setProductStatus(ProductStatusEnum.UP.getCode());

        return repository.save(productInfo);
    }

    @Override
    public ProductInfo offSale(Long productId) {

        ProductInfo productInfo = this.findOne(productId);

        // 判断商品状态是否正确
        if (productInfo.getProductStatus().equals(ProductStatusEnum.DOWN.getCode())) {

            log.error("【下架商品】商品状态不正确，productId={}", productId);
            throw new SellException(ResultEnum.PRODUCT_STATUS_ERROR);
        }

        productInfo.setProductStatus(ProductStatusEnum.DOWN.getCode());

        return repository.save(productInfo);
    }
}
