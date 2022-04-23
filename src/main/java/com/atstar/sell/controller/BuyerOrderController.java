package com.atstar.sell.controller;

import com.atstar.sell.VO.ResultVO;
import com.atstar.sell.converter.OrderForm2OrderDTOConverter;
import com.atstar.sell.dto.OrderDTO;
import com.atstar.sell.enums.ResultEnum;
import com.atstar.sell.exception.SellException;
import com.atstar.sell.form.OrderForm;
import com.atstar.sell.service.OrderService;
import com.atstar.sell.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Dawn
 * @Date: 2022/4/16 17:29
 */
@Slf4j
@RestController
@RequestMapping("/buyer/order")
public class BuyerOrderController {

    @Resource
    private OrderService orderService;

    // 创建订单
    @PostMapping("/create")
    public ResultVO<Map<String, Long>> create(@Valid OrderForm orderForm) {

        OrderDTO orderDTO = OrderForm2OrderDTOConverter.convert(orderForm);

        if (CollectionUtils.isEmpty(orderDTO.getOrderDetailList())) {
            log.error("【创建订单】购物车不能为空");
            throw new SellException(ResultEnum.CART_EMPTY);
        }

        OrderDTO result = orderService.save(orderDTO);

        Map<String, Long> map = new HashMap<>();
        map.put("orderId", result.getOrderId());

        return ResultVOUtil.success(map);
    }

    // 订单列表
    @GetMapping("/list")
    public ResultVO<List<OrderDTO>> list(@RequestParam("openid") String openid,
                                         @RequestParam(required = false, defaultValue = "0") Integer page,
                                         @RequestParam(required = false, defaultValue = "10") Integer size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createTime").descending());

        Page<OrderDTO> orderDTOPage = orderService.listOrderDTO(openid, pageRequest);

        return ResultVOUtil.success(orderDTOPage.getContent());
    }

    @GetMapping("/detail")
    public ResultVO<OrderDTO> detail(@RequestParam("openid") String openid,
                                     @RequestParam("orderId") Long orderId) {

        OrderDTO orderDTO = orderService.findOne(openid, orderId);

        return ResultVOUtil.success(orderDTO);
    }

    @PostMapping("/cancel")
    public ResultVO<OrderDTO> cancel(@RequestParam("openid") String openid,
                                     @RequestParam("orderId") Long orderId) {

        orderService.cancel(openid, orderId);

        return ResultVOUtil.success();
    }
}
