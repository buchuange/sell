package com.atstar.sell.service;

import com.atstar.sell.dto.OrderDTO;

/**
 * 推送模板消息
 */
public interface PushMsgService {

    /**
     * 订单状态变更消息
     */
    void pushOrderStatus(OrderDTO orderDTO);
}
