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

        // 1????????????????????????????????????
        List<Long> productIdList = orderDTO.getOrderDetailList()
                .stream().map(OrderDetail::getProductId).collect(Collectors.toList());

        List<ProductInfo> productInfoList = productService.findByProductIdIn(productIdList);

        Map<Long, ProductInfo> productInfoMap = productInfoList.stream()
               .collect(Collectors.toMap(ProductInfo::getProductId, productInfo -> productInfo));

        // 2???????????????
        BigDecimal orderAmount = BigDecimal.ZERO;

        Long orderId = KeyUtil.generateUniqueKey();

        for (OrderDetail orderDetail : orderDTO.getOrderDetailList()) {

            ProductInfo productInfo = productInfoMap.get(orderDetail.getProductId());

            // ????????????????????????
            if (ObjectUtils.isEmpty(productInfo)) {
                throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
            }

            orderAmount = new BigDecimal(orderDetail.getProductQuantity())
                    .multiply(productInfo.getProductPrice()).add(orderAmount);

            orderDetail.setOrderId(orderId);
            BeanUtils.copyProperties(productInfo, orderDetail);
        }

        // 3?????????????????? ???????????? ??? ???????????????
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderId(orderId);
        BeanUtils.copyProperties(orderDTO, orderMaster);
        orderMaster.setOrderAmount(orderAmount);
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT_PAID.getCode());
        orderMasterRepository.save(orderMaster);

        orderDetailRepository.saveAll(orderDTO.getOrderDetailList());

        // 4????????????
        List<CartDTO> cartDTOList = orderDTO.getOrderDetailList().stream()
                .map(o -> new CartDTO(o.getProductId(), o.getProductQuantity()))
                .collect(Collectors.toList());

        productService.decreaseStock(cartDTOList);

        // 5?????????websocket??????
        webSocket.sendMessage("?????????????????????");

        return orderDTO;
    }

    @Override
    public OrderDTO findOne(String buyerOpenid, Long orderId) {

        OrderMaster orderMaster = orderMasterRepository.findById(orderId).orElse(null);

        if (ObjectUtils.isEmpty(orderMaster) || !buyerOpenid.equals(orderMaster.getBuyerOpenid())) {

            log.error("????????????????????????????????????openid={}, orderId={}", buyerOpenid, orderId);
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
            // ??????????????????????????????????????????????????? ??????????????????????????????
            log.error("?????????????????????????????????????????????, orderId={}", orderId);
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

            log.error("????????????????????????????????????openid={}, orderId={}", buyerOpenid, orderId);
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }

        // ??????????????????
        if (!orderMaster.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())) {
            log.error("??????????????????????????????????????????orderId={}, orderStatus={}", orderId, orderMaster.getOrderStatus());

            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }

        // ??????????????????
        orderMaster.setOrderStatus(OrderStatusEnum.CANCELED.getCode());
        OrderMaster result = orderMasterRepository.save(orderMaster);
        if (ObjectUtils.isEmpty(result)) {

            log.error("??????????????????????????????, orderMaster={}", gson.toJson(orderMaster));
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        // ?????????
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderId);
        if (CollectionUtils.isEmpty(orderDetailList)) {

            log.error("?????????????????????????????????????????????, orderMaster={}", gson.toJson(orderMaster));
            throw new SellException(ResultEnum.ORDER_DETAIL_EMPTY);
        }

        List<CartDTO> cartDTOList = orderDetailList.stream()
                .map(e -> new CartDTO(e.getProductId(), e.getProductQuantity()))
                .collect(Collectors.toList());
        productService.increaseStock(cartDTOList);

        OrderDTO orderDTO = OrderMaster2OrderDTOConverter.convert(orderMaster);
        orderDTO.setOrderDetailList(orderDetailList);

        // ??????????????????????????????
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

            log.error("????????????????????????????????????orderId={}", orderId);
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }

        // ??????????????????
        if (!orderMaster.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())) {
            log.error("??????????????????????????????????????????orderId={}, orderStatus={}", orderId, orderMaster.getOrderStatus());

            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }

        // ??????????????????
        orderMaster.setOrderStatus(OrderStatusEnum.TRADE_SUCCESS.getCode());
        OrderMaster result = orderMasterRepository.save(orderMaster);
        if (ObjectUtils.isEmpty(result)) {

            log.error("??????????????????????????????, orderMaster={}", gson.toJson(orderMaster));
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        OrderDTO orderDTO = OrderMaster2OrderDTOConverter.convert(orderMaster);

        // ????????????????????????
        pushMsgService.pushOrderStatus(orderDTO);


        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO paid(Long orderId) {

        OrderMaster orderMaster = orderMasterRepository.findById(orderId).orElse(null);

        if (ObjectUtils.isEmpty(orderMaster)) {

            log.error("??????????????????????????????????????????orderId={}", orderId);
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }

        // ??????????????????
        if (!orderMaster.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())) {
            log.error("????????????????????????????????????????????????orderId={}, orderStatus={}", orderId, orderMaster.getOrderStatus());

            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }

        // ????????????????????????
        if (!orderMaster.getPayStatus().equals(PayStatusEnum.WAIT_PAID.getCode())) {

            log.error("????????????????????????????????????????????????orderId={}, payStatus={}", orderId, orderMaster.getPayStatus());

            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }

        // ????????????????????????
        orderMaster.setPayStatus(PayStatusEnum.PAID_SUCCESS.getCode());
        OrderMaster result = orderMasterRepository.save(orderMaster);
        if (ObjectUtils.isEmpty(result)) {

            log.error("????????????????????????????????????, orderMaster={}", gson.toJson(orderMaster));
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }

        return OrderMaster2OrderDTOConverter.convert(orderMaster);
    }

    public List<OrderDetail> checkOrderDetail(Long orderId) {

        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderId);
        if (CollectionUtils.isEmpty(orderDetailList)) {

            log.error("????????????????????????????????????, orderId={}", orderId);
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
