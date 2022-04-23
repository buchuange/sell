package com.atstar.sell.controller.seller;

import com.atstar.sell.dto.OrderDTO;
import com.atstar.sell.enums.ResultEnum;
import com.atstar.sell.exception.SellException;
import com.atstar.sell.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

/**
 * 卖家端订单
 *
 * @Author: Dawn
 * @Date: 2022/4/19 22:53
 */
@Slf4j
@Controller
@RequestMapping("/seller/order")
public class SellerOrderController {

    @Resource
    private OrderService orderService;


    /**
     * 查询订单列表
     *
     * @param page 第几页 从1开始
     * @param size 每页几条数据
     * @return
     */
    @GetMapping("/list")
    public String list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                       @RequestParam(value = "size", defaultValue = "10") Integer size, Model model) {

        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("createTime").descending());

        Page<OrderDTO> orderDTOPage = orderService.listOrderDTO(pageRequest);

        model.addAttribute("orderDTOPage", orderDTOPage);
        model.addAttribute("currentPage", page);

        return "order/list";
    }

    /**
     * 取消订单
     */
    @GetMapping("/cancel/{orderId}")
    public String cancel(@PathVariable("orderId") Long orderId, Model model) {

        try {
            OrderDTO orderDTO = orderService.findByOrderId(orderId);
            orderService.cancel(orderDTO.getBuyerOpenid(), orderId);
        } catch (SellException e) {
            log.error("【卖家取消订单失败】，msg={}", e.getMsg());

            model.addAttribute("msg", e.getMsg());
            model.addAttribute("url", "/seller/order/list");

            return "common/error";
        }

        model.addAttribute("msg", ResultEnum.ORDER_CANCEL_SUCCESS.getMsg());
        model.addAttribute("url", "/seller/order/list");

        return "common/success";
    }

    /**
     * 取消订单
     */
    @GetMapping("/detail/{orderId}")
    public String detail(@PathVariable("orderId") Long orderId, Model model) {

        OrderDTO orderDTO;

        try {

            orderDTO = orderService.findByOrderId(orderId);

        } catch (SellException e) {
            log.error("【卖家查询订单详情】，发生异常 msg={}", e.getMsg());

            model.addAttribute("msg", e.getMsg());
            model.addAttribute("url", "/seller/order/list");

            return "common/error";
        }

        model.addAttribute("orderDTO", orderDTO);

        return "order/detail";
    }

    @GetMapping("/finish/{orderId}")
    public String finish(@PathVariable("orderId") Long orderId, Model model) {

        try {

            orderService.finish(orderId);

        } catch (SellException e) {
            log.error("【卖家端完结订单】，发生异常 msg={}", e.getMsg());

            model.addAttribute("msg", e.getMsg());
            model.addAttribute("url", "/seller/order/list");

            return "common/error";
        }

        model.addAttribute("msg", ResultEnum.ORDER_FINISH_SUCCESS.getMsg());
        model.addAttribute("url", "/seller/order/list");

        return "common/success";
    }
}
