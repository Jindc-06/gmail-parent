package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.model.enums.PaymentStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.client.OrderFeignClient;
import com.atguigu.gmall.payment.config.AlipayConfig;
import com.atguigu.gmall.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Date 2021/6/7 20:17
 * @Author JINdc
 **/
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    OrderFeignClient orderFeignClient;
    @Autowired
    PaymentInfoMapper paymentInfoMapper;
    @Autowired
    AlipayClient alipayClient;
    @Override
    public String alipayFrom(String userId, String orderId) {
        //调用订单服务,获取订单的信息
        OrderInfo orderInfo = orderFeignClient.getOrderById(orderId);
        //调用alipayAPI ,生成支付页面
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        Map<String,Object> map = new HashMap<>();
        map.put("out_trade_no",orderInfo.getOutTradeNo());
        //alipay的产品
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        //设置商品价格
        map.put("total_amount",0.01);
        //商品名
        map.put("subject",orderInfo.getOrderDetailList().get(0).getSkuName());
        //将map转换成String
        alipayRequest.setBizContent(JSON.toJSONString(map));
        String from = "";
        try {
            //生成表单
            from = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setTotalAmount(orderInfo.getTotalAmount());
        paymentInfo.setOrderId(Long.parseLong(orderId));
        paymentInfo.setSubject(orderInfo.getOrderDetailList().get(0).getSkuName());
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.toString());
        //回调时间 回调信息 todo
        //将支付信息保存到支付表
        paymentInfoMapper.insert(paymentInfo);
        return from;
    }
}
