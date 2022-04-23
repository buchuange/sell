package com.atstar.sell.service;

import com.atstar.sell.dto.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    // 买家端
    /**
     * 创建订单
     */
    OrderDTO save(OrderDTO orderDTO);

    /**
     * 查询单个订单
     */
    OrderDTO findOne(String buyerOpenid, Long orderId);

    OrderDTO findByOrderId(Long orderId);

    /**
     * 查询订单列表
     */
    Page<OrderDTO> listOrderDTO(String buyerOpenid, Pageable pageable);

    /**
     * 取消订单
     */
    OrderDTO cancel(String buyerOpenid, Long orderId);


    /**
     * 支付订单
     */
    OrderDTO paid(Long orderId);

    // 卖家端

    /**
     * 查询订单列表
     */
    Page<OrderDTO> listOrderDTO(Pageable pageable);

    /**
     * 完结订单
     */
    OrderDTO finish(Long orderId);
}
