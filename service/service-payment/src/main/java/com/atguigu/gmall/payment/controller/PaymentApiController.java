package com.atguigu.gmall.payment.controller;

import com.atguigu.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Date 2021/6/5 17:04
 * @Author JINdc
 **/
@RestController
@RequestMapping("api/payment")
public class PaymentApiController {

    @Autowired
    PaymentService paymentService;

    //支付宝支付
    @RequestMapping("alipay/submit/{orderId}")
    public String alipay(HttpServletRequest request, @PathVariable("orderId") String orderId){
        String userId = request.getHeader("userId");
        String from = paymentService.alipayFrom(userId,orderId);
        return from;
    }

}
