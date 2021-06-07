package com.atguigu.gmall.order.client;

import com.atguigu.gmall.model.order.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Date 2021/6/5 14:50
 * @Author JINdc
 **/
@FeignClient(value = "service-order")
public interface OrderFeignClient {
    //订单数据
    @RequestMapping("api/order/getTradeOrder")
    OrderInfo getTradeOrder();
    //订单号
    @RequestMapping("api/order/getTradeNo")
    String getTradeNo();
    //调用订单服务,获取订单的信息
    @RequestMapping("api/order/getOrderById/{orderId}")
    OrderInfo getOrderById(@PathVariable("orderId") String orderId);
}
