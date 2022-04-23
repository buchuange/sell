package com.atstar.sell.service.impl;

import com.atstar.sell.SellApplicationTests;
import com.atstar.sell.domain.OrderDetail;
import com.atstar.sell.dto.OrderDTO;
import com.atstar.sell.enums.OrderStatusEnum;
import com.atstar.sell.enums.PayStatusEnum;
import com.atstar.sell.service.OrderService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OrderServiceImplTest extends SellApplicationTests {

    @Resource
    private OrderService orderService;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void save() {

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBuyerName("马云");
        orderDTO.setBuyerPhone("1414646421");
        orderDTO.setBuyerAddress("未知之地");
        orderDTO.setBuyerOpenid("ew3e1232345w9diwkq");

        List<OrderDetail> orderDetailList = new ArrayList<>();
        OrderDetail orderDetail1 = new OrderDetail(1000001L, 2);
        orderDetailList.add(orderDetail1);
        OrderDetail orderDetail2 = new OrderDetail(1000002L, 2);
        orderDetailList.add(orderDetail2);

        orderDTO.setOrderDetailList(orderDetailList);

        OrderDTO result = orderService.save(orderDTO);
        log.info(gson.toJson(result));

        Assertions.assertNotNull(result);
    }

    @Test
    public void findOne() {

        OrderDTO orderDTO = orderService.findOne("ew3euwhd7sjw9diwkq", 1650041053037L);

        log.info(gson.toJson(orderDTO));

        Assertions.assertNotNull(orderDTO);
    }

    @Test
    public void listOrderDTO() {

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("createTime").descending());

        Page<OrderDTO> orderDTOPage = orderService.listOrderDTO("ew3euwhd7sjw9diwkq", pageRequest);

        log.info(gson.toJson(orderDTOPage));
    }

    @Test
    public void cancel() {

        OrderDTO orderDTO = orderService.cancel("ew3euwhd7sjw9diwkq", 1650041053037L);

        log.info(gson.toJson(orderDTO));

        Assertions.assertEquals(OrderStatusEnum.CANCELED.getCode(), orderDTO.getOrderStatus());
    }

    @Test
    public void finish() {

        OrderDTO orderDTO = orderService.finish(1649951592738L);

        log.info(gson.toJson(orderDTO));

        Assertions.assertEquals(OrderStatusEnum.TRADE_SUCCESS.getCode(), orderDTO.getOrderStatus());
    }

    @Test
    public void paid() {

        OrderDTO orderDTO = orderService.paid(1649951592738L);

        log.info(gson.toJson(orderDTO));

        Assertions.assertEquals(PayStatusEnum.PAID_SUCCESS.getCode(), orderDTO.getPayStatus());

    }

    @Test
    public void testListOrderDTO() {

        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("updateTime").descending());

        Page<OrderDTO> orderDTOPage = orderService.listOrderDTO(pageRequest);

        log.info(gson.toJson(orderDTOPage));
        log.info(gson.toJson(orderDTOPage.getPageable().hasPrevious()));
    }
}