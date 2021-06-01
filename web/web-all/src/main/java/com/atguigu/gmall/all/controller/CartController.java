package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URLEncoder;

/**
 * @Date 2021/5/31 13:57
 * @Author JINdc
 **/
@Controller
public class CartController {

    @Autowired
    CartFeignClient cartFeignClient;
    //购物车列表
    @RequestMapping("cart/cart.html")
    public String cartList(Model model){
        return "cart/index";
    }
    //加入购物车
    @RequestMapping("addCart.html")
    public String addCart(Long skuId,Long skuNum){
        //调用service-cart服务
        CartInfo cartInfo = cartFeignClient.addCart(skuId,skuNum);
        //url将页面参数传递  URLEncoder.encode(解决参数乱码)
        return "redirect:http://cart.gmall.com/cart/addCart.html?skuName"+ URLEncoder.encode(cartInfo.getSkuName())+"&skuDefaultImg="+URLEncoder.encode(cartInfo.getImgUrl());
    }
    //购物车复选框
//    @RequestMapping("cart/cart.html")
//    public String checkCart(){
//        return "cart/index";
//    }

}
