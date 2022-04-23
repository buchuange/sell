package com.atstar.sell.controller;

import com.atstar.sell.dto.OrderDTO;
import com.atstar.sell.service.OrderService;
import com.atstar.sell.service.PayService;
import com.lly835.bestpay.model.PayResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author: Dawn
 * @Date: 2022/4/18 17:25
 */
@Controller
@RequestMapping("/pay")
public class PayController {

    @Resource
    private OrderService orderService;

    @Resource
    private PayService payService;

    @GetMapping("/create")
    public String create(@RequestParam("orderId") Long orderId,
                         @RequestParam("returnUrl") String returnUrl,
                         Model model) {

        // 查询订单
        OrderDTO orderDTO = orderService.findByOrderId(orderId);

        // 发起支付
        PayResponse payResponse = payService.create(orderDTO);

        model.addAttribute("payResponse", payResponse);
        model.addAttribute("returnUrl", returnUrl);

        return "pay/create";
    }

    @PostMapping("/asyncNotify")
    public String asyncNotify(@RequestBody String notifyData) {

        payService.asyncNotify(notifyData);

        // 返回给微信处理结果
        return "pay/success";
    }
}
