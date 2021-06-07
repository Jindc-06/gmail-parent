package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.util.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Date 2021/6/5 14:46
 * @Author JINdc
 **/
@RestController
@RequestMapping("api/order")
public class OrderApiController {

    @Autowired
    OrderService orderService;

    //订单数据
    @RequestMapping("getTradeOrder")
    OrderInfo getTradeOrder(HttpServletRequest request){
        String userId = request.getHeader("userId");
        OrderInfo orderInfo = orderService.getTradeOrder(userId);
        return orderInfo;
    }
    //贸易码,用来确认订单是否提交,避免重复和提交退回
    @RequestMapping("getTradeNo")
    String getTradeNo(HttpServletRequest request){
        String userId = request.getHeader("userId");
        String tradeNo = orderService.getTradeNo(userId);
        return tradeNo;
    }

    //提交订单,保存订单信息
    @RequestMapping("auth/submitOrder")
    Result submitOrder(HttpServletRequest request, @RequestBody OrderInfo orderInfo,String tradeNo){
        String userId = request.getHeader("userId");
        //提交的购物车物品信息,查询是否提交
        boolean isCheck = orderService.checkTradeNo(userId,tradeNo);
        if (isCheck){//如果已经提交
            //保存订单信息
            orderInfo.setUserId(Long.parseLong(userId));
            String orderId = orderService.submitOrder(orderInfo);
            //返回订单编号
            return Result.ok(orderId);
        }else {
            return Result.fail();
        }
    }
    //支付服务调用订单服务,获取订单的信息
    @RequestMapping("getOrderById/{orderId}")
    OrderInfo getOrderById(@PathVariable("orderId") String orderId){
        OrderInfo orderInfo = orderService.getOrderById(orderId);
        return orderInfo;
    }
}
