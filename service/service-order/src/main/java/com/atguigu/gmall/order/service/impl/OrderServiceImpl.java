package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Date 2021/6/5 14:47
 * @Author JINdc
 **/
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    CartFeignClient cartFeignClient;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    OrderInfoMapper orderInfoMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;
    @Autowired
    ProductFeignClient productFeignClient;

    @Override
    public OrderInfo getTradeOrder(String userId) {
        OrderInfo orderInfo = new OrderInfo();
        //获取购物车的信息
        List<CartInfo> cartInfoList = cartFeignClient.getTradeOrder(userId);
        if (cartInfoList!=null && cartInfoList.size()>0){
            List<OrderDetail> orderDetailList = new ArrayList<>();
            for (CartInfo cartInfo : cartInfoList) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setSkuId(cartInfo.getSkuId());
                //orderDetail.setHasStock();
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                orderDetail.setOrderPrice(cartInfo.getCartPrice());
                orderDetail.setSkuNum(cartInfo.getSkuNum());
                orderDetail.setSkuName(cartInfo.getSkuName());
                orderDetailList.add(orderDetail);
            }
            orderInfo.setOrderDetailList(orderDetailList);
        }
        return orderInfo;
    }

    @Override
    public String getTradeNo(String userId) {
        //生成交易编号
        String tradeNo = UUID.randomUUID().toString();
        //缓存到redis
        redisTemplate.opsForValue().set("user:"+userId+":tradeNo",tradeNo);

        return tradeNo;
    }

    @Override
    public boolean checkTradeNo(String userId, String tradeNo) {
        //查询缓存中是否有该交易码
        String tradeNoDB = (String) redisTemplate.opsForValue().get("user:"+userId+":tradeNo");
        if (!StringUtils.isEmpty(tradeNoDB) && tradeNo.equals(tradeNoDB)){
            //删除交易码
            redisTemplate.delete("user:"+userId+":tradeNo");
            return true;
        }else {
            return false;
        }
    }

    @Override
    public String submitOrder(OrderInfo orderInfo) {


        //订单创建时间
        orderInfo.setCreateTime(new Date());
        //送货地址
        //orderInfo.setDeliveryAddress();
        //订单未付款失效时间(24小时过期)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        Date time = calendar.getTime();
        orderInfo.setExpireTime(time);
        //订单备注
        String orderComment = "hello";
        orderInfo.setOrderComment(orderComment);
        //订单进度
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.toString());
        //当前订单的交易号(唯一随机数组)
        String  outTradeNo = "J"+System.currentTimeMillis()+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        orderInfo.setOutTradeNo(outTradeNo);
        //订单总金额
        BigDecimal totalAmount = new BigDecimal("0");
        for (OrderDetail orderDetail : orderInfo.getOrderDetailList()) {
            totalAmount = totalAmount.add(orderDetail.getOrderPrice());
        }
        orderInfo.setTotalAmount(totalAmount);
        orderInfoMapper.insert(orderInfo);
        //订单表主键id
        Long orderId = orderInfo.getId();
        for (OrderDetail orderDetail : orderInfo.getOrderDetailList()) {
            orderDetail.setOrderId(orderId);
            //验价格(加入时的价格和当前价格)todo
            SkuInfo currentSkuInfo = productFeignClient.getSkuById(orderDetail.getSkuId());
            BigDecimal currentPrice = currentSkuInfo.getPrice();
            BigDecimal orderPrice = orderDetail.getOrderPrice();
            if (orderPrice!=null && currentPrice!= orderPrice){

            }
            //验证库存 todo
            // 一旦价格和库存发生变化，方法回滚，取消订单提交业务，用户重新回到购物车确认
            orderDetailMapper.insert(orderDetail);
            //提交订单之后,删除购物车的该商品的信息 todo

        }

        return orderInfo+"";
    }

    @Override
    public OrderInfo getOrderById(String orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        QueryWrapper<OrderDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",orderId);
        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(wrapper);
        orderInfo.setOrderDetailList(orderDetailList);
        return orderInfo;
    }
}
