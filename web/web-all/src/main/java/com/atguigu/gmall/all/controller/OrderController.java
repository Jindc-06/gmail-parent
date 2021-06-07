package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.client.UserFeignClient;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.order.client.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Date 2021/6/5 14:52
 * @Author JINdc
 **/
@Controller
public class OrderController {

    @Autowired
    OrderFeignClient orderfeignClient;
    @Autowired
    UserFeignClient userFeignClient;

    //订单页面
    @RequestMapping("trade.html")
    public String trade(Model model){
        //订单数据
        OrderInfo orderInfo = orderfeignClient.getTradeOrder();
        //订单明细
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        //获取用户地址
        List<UserAddress> userAddressList = userFeignClient.getUserAddresses();
        model.addAttribute("detailArrayList",orderDetailList);
        model.addAttribute("userAddressList",userAddressList);
        //设置收货人
        orderInfo.setConsignee(userAddressList.get(0).getConsignee());
        //设置收货人电话
        orderInfo.setConsigneeTel(userAddressList.get(0).getPhoneNum());
        model.addAttribute("order",orderInfo);
        //订单号
        String tradeNo = orderfeignClient.getTradeNo();
        model.addAttribute("tradeNo",tradeNo);
        return "order/trade";
    }
}
