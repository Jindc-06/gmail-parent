package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.model.order.OrderInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Date 2021/6/5 14:52
 * @Author JINdc
 **/
@Controller
public class PaymentController {

    @RequestMapping("pay.html")
    public String pay(String orderId, Model model){
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(Long.parseLong(orderId));
        model.addAttribute("orderInfo",orderInfo);
        return "payment/pay";
    }
}
