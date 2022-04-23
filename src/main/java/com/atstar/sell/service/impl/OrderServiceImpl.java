package com.atstar.sell.service.impl;

import com.atstar.sell.controller.seller.WebSocket;
import com.atstar.sell.converter.OrderMaster2OrderDTOConverter;
import com.atstar.sell.domain.OrderDetail;
import com.atstar.sell.domain.OrderMaster;
import com.atstar.sell.domain.ProductInfo;
import com.atstar.sell.dto.CartDTO;
import com.atstar.sell.dto.OrderDTO;
import com.atstar.sell.enums.OrderStatusEnum;
import com.atstar.sell.enums.PayStatusEnum;
import com.atstar.sell.enums.ResultEnum;
import com.atstar.sell.exception.SellException;
import com.atstar.sell.repository.OrderDetailRepository;
import com.atstar.sell.repository.OrderMasterRepository;
import com.atstar.sell.repository.ProductInfoRepository;
import com.atstar.sell.service.OrderService;
import com.atstar.sell.service.PayService;
import com.atstar.sell.service.ProductService;
import com.atstar.sell.service.PushMsgService;
import com.atstar.sell.utils.KeyUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: Dawn
 * @Date: 2022/4/14 01:31
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMasterRepository orderMasterRepository;

    @Resource
    private OrderDetailRepository orderDetailRepository;

    @Resource
    private ProductService productService;

    @Resource
    private PayService payService;

    @Resource
    private PushMsgService pushMsgService;

    @Resource
    private WebSocket webSocket;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    @Transactional
    public OrderDTO save(OrderDTO orderDTO) {

        // 1、查询商品（数量、价格）
        List<Long> productIdList = orderDTO.getOrderDetailList()
                .stream().map(OrderDetail::getProductId).collect(Collectors.toList());

        List<ProductInfo> productInfoList = productService.findByProductIdIn(productIdList);

        Map<Long, ProductInfo> productInfoMap = productInfoList.stream()
               .collect(Collectors.toMap(ProductInfo::getProductId, productInfo -> productInfo));

        // 2、计算总价
        BigDecimal orderAmount = BigDecimal.ZERO;

        Long orderId = KeyUtil.generateUniqueKey();

        for (OrderDetail orderDetail : orderDTO.getOrderDetailList()) {

            ProductInfo productInfo = productInfoMap.get(orderDetail.getProductId());

            // 判断商品是否存在
            if (ObjectUtils.isEmpty(productInfo)) {
                throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
            }

            orderAmount = new BigDecimal(orderDetail.getProductQuantity())
                    .multiply(productInfo.getProductPrice()).add(orderAmount);

            orderDetail.setOrderId(orderId);
            BeanUtils.copyProperties(productInfo, orderDetail);
        }

        // 3、写入数据库 订单主表 和 订单详情表
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderId(orderId);
        BeanUtils.copyProperties(orderDTO, orderMaster);
        orderMaster.setOrderAmount(orderAmount);
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT_PAID.getCode());
        orderMasterRepository.save(orderMaster);

        orderDetailRepository.saveAll(orderDTO.getOrderDetailList());

        // 4、扣库存
        List<CartDTO> cartDTOList = orderDTO.getOrderDetailList().stream()
                .map(o -> new CartDTO(o.getProductId(), o.getProductQuantity()))
                .collect(Collectors.toList());

        productService.decreaseStock(cartDTOList);

        // 5、发送websocket消息
        webSocket.sendMessage("您有新的订单了");

        return orderDTO;
    }

    @Override
    public OrderDTO findOne(String buyerOpenid, Long orderId) {

        OrderMaster orderMaster = orderMasterRepository.findById(orderId).orElse(null);

        if (ObjectUtils.isEmpty(orderMaster) || !buyerOpenid.equals(orderMaster.getBuyerOpenid())) {

            log.error("【查询订单】订单不存在，openid={}, orderId={}", buyerOpenid, orderId);
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }

        List<OrderDetail> orderDetailList = checkOrderDetail(orderId);

        OrderDTO orderDTO = OrderMaster2OrderDTOConverter.convert(orderMaster);
        orderDTO.setOrderDetailList(orderDetailList);

        return orderDTO;
    }

    @Override
    public OrderDTO findByOrderId(Long orderId) {

        OrderMaster orderMaster = orderMasterRepository.findById(orderId).orElse(null);

        if (ObjectUtils.isEmpty(orderMaster)) {
            // 比较严重（正常情况下是不会发生的） 发出告警：钉钉、短信
            log.error("【微信支付查询订单】订单不存在, orderId={}", orderId);
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }

        List<OrderDetail> orderDetailList = checkOrderDetail(orderId);

        OrderDTO orderDTO = OrderMaster2OrderDTOConverter.convert(orderMaster);
        orderDTO.setOrderDetailList(orderDetailList);

        return orderDTO;
    }

    @Override
    public Page<OrderDTO> listOrderDTO(String buyerOpenid, Pageable pageable) {

        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByBuyerOpenid(buyerOpenid, pageable);

        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

        return new PageImpl<>(orderDTOList, orderMasterPage.getPageable(), orderMasterPage.getTotalElements());
    }

    @Override
    @Transactional
    public OrderDTO cancel(String buyerOpenid, Long orderId) {

        OrderMaster orderMaster = orderMasterRepository.findById(orderId).orElse(null);

        if (ObjectUtils.isEmpty(orderMaster) || !buyerOpenid.equals(orderMaster.getBuyerOpenid())) {

            log.error("【取消订单】订单不存在，openid={}, orderId={}", buyerOpenid, orderId);
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }

        // 判断订单状态
        if (!orderMaster.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())) {
            log.error("【取消订单】订单状态不正确，orderId={}, orderStatus={}", orderId, orderMaster.getOrderStatus());

            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }

        // 修改订单状态
        orderMaster.setOrderStatus(OrderStatusEnum.CANCELED.getCode());
        OrderMaster result = orderMasterRepository.save(orderMaster);
        if (ObjectUtils.isEmpty(result)) {

            log.error("【取消订单】更新失败, orderMaster={}", gson.toJson(orderMaster));
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        // 加库存
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderId);
        if (CollectionUtils.isEmpty(orderDetailList)) {

            log.error("【取消订单】订单中没有商品详情, orderMaster={}", gson.toJson(orderMaster));
            throw new SellException(ResultEnum.ORDER_DETAIL_EMPTY);
        }

        List<CartDTO> cartDTOList = orderDetailList.stream()
                .map(e -> new CartDTO(e.getProductId(), e.getProductQuantity()))
                .collect(Collectors.toList());
        productService.increaseStock(cartDTOList);

        OrderDTO orderDTO = OrderMaster2OrderDTOConverter.convert(orderMaster);
        orderDTO.setOrderDetailList(orderDetailList);

        // 如果已支付，需要退款
        if (orderMaster.getPayStatus().equals(PayStatusEnum.PAID_SUCCESS.getCode())) {
            //TODO
            payService.refund(orderDTO);
        }


        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO finish(Long orderId) {

        OrderMaster orderMaster = orderMasterRepository.findById(orderId).orElse(null);

        if (ObjectUtils.isEmpty(orderMaster)) {

            log.error("【完结订单】订单不存在，orderId={}", orderId);
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }

        // 判断订单状态
        if (!orderMaster.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())) {
            log.error("【完结订单】订单状态不正确，orderId={}, orderStatus={}", orderId, orderMaster.getOrderStatus());

            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }

        // 修改订单状态
        orderMaster.setOrderStatus(OrderStatusEnum.TRADE_SUCCESS.getCode());
        OrderMaster result = orderMasterRepository.save(orderMaster);
        if (ObjectUtils.isEmpty(result)) {

            log.error("【完结订单】更新失败, orderMaster={}", gson.toJson(orderMaster));
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        OrderDTO orderDTO = OrderMaster2OrderDTOConverter.convert(orderMaster);

        // 推送微信模板消息
        pushMsgService.pushOrderStatus(orderDTO);


        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO paid(Long orderId) {

        OrderMaster orderMaster = orderMasterRepository.findById(orderId).orElse(null);

        if (ObjectUtils.isEmpty(orderMaster)) {

            log.error("【订单支付完成】订单不存在，orderId={}", orderId);
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }

        // 判断订单状态
        if (!orderMaster.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())) {
            log.error("【订单支付完成】订单状态不正确，orderId={}, orderStatus={}", orderId, orderMaster.getOrderStatus());

            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }

        // 判断订单支付状态
        if (!orderMaster.getPayStatus().equals(PayStatusEnum.WAIT_PAID.getCode())) {

            log.error("【订单支付完成】支付状态不正确，orderId={}, payStatus={}", orderId, orderMaster.getPayStatus());

            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }

        // 修改订单支付状态
        orderMaster.setPayStatus(PayStatusEnum.PAID_SUCCESS.getCode());
        OrderMaster result = orderMasterRepository.save(orderMaster);
        if (ObjectUtils.isEmpty(result)) {

            log.error("【订单支付完成】更新失败, orderMaster={}", gson.toJson(orderMaster));
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        return OrderMaster2OrderDTOConverter.convert(orderMaster);
    }

    public List<OrderDetail> checkOrderDetail(Long orderId) {

        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderId);
        if (CollectionUtils.isEmpty(orderDetailList)) {

            log.error("【查询订单】订单详情为空, orderId={}", orderId);
            throw new SellException(ResultEnum.ORDER_DETAIL_NOT_EXIST);
        }

        return orderDetailList;
    }

    @Override
    public Page<OrderDTO> listOrderDTO(Pageable pageable) {

        Page<OrderMaster> orderMasterPage = orderMasterRepository.findAll(pageable);

        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());

        return new PageImpl<>(orderDTOList, orderMasterPage.getPageable(), orderMasterPage.getTotalElements());
    }
}
