package com.atstar.sell.converter;

import com.atstar.sell.domain.OrderMaster;
import com.atstar.sell.dto.OrderDTO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Dawn
 * @Date: 2022/4/16 01:50
 */
public class OrderMaster2OrderDTOConverter {

    public static OrderDTO convert(OrderMaster orderMaster) {

        OrderDTO orderDTO = new OrderDTO();

        BeanUtils.copyProperties(orderMaster, orderDTO);

        return orderDTO;
    }

    public static List<OrderDTO> convert(List<OrderMaster> orderMasterList) {

        return orderMasterList.stream().map(OrderMaster2OrderDTOConverter::convert).collect(Collectors.toList());
    }
}
