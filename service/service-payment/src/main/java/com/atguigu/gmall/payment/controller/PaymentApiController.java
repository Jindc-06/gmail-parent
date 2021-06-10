package com.atguigu.gmall.payment.controller;

import com.atguigu.gmall.model.payment.PaymentInfo;
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
    //支付宝回调
    @RequestMapping("alipay/callback/return")
    public String callback(HttpServletRequest request){
        String out_order_no = request.getParameter("out_order_no");
        String trade_no = request.getParameter("trade_no");
        String callback_content = request.getQueryString();
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(out_order_no);
        paymentInfo.setTradeNo(trade_no);
        paymentInfo.setCallbackContent(callback_content);
        //修改支付状态
        //paymentService.updatePayment(paymentInfo);
        //成功支付后,发送消息队列
        return "支付成功";
    }
    //支付宝支付(表单)
    @RequestMapping("alipay/submit/{orderId}")
    public String alipay(HttpServletRequest request, @PathVariable("orderId") String orderId){
        String userId = request.getHeader("userId");
        String from = paymentService.alipayFrom(userId,orderId);
        return from;
    }

}
