package com.atguigu.gmall.payment.service;

/**
 * @Date 2021/6/7 20:17
 * @Author JINdc
 **/
public interface PaymentService {
    String alipayFrom(String userId, String orderId);
}
