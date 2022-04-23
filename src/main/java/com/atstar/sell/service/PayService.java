package com.atstar.sell.service;

import com.atstar.sell.dto.OrderDTO;
import com.lly835.bestpay.model.PayResponse;

public interface PayService {

    PayResponse create(OrderDTO orderDTO);

    void asyncNotify(String notifyData);

    void refund(OrderDTO orderDTO);
}
