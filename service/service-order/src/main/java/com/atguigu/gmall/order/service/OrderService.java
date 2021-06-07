package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

/**
 * @Date 2021/6/5 14:47
 * @Author JINdc
 **/
public interface OrderService {
    OrderInfo getTradeOrder(String userId);

    String getTradeNo(String userId);

    boolean checkTradeNo(String userId, String tradeNo);

    String submitOrder(OrderInfo orderInfo);

    OrderInfo getOrderById(String orderId);
}
