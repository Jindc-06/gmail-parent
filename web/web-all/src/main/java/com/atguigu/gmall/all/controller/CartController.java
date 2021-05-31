package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String cartList(){
        return "cart/index";
    }
    //加入购物车
    @RequestMapping("addCart.html")
    public String addCart(Long skuId,Long skuNum){
        //调用service-cart服务
        CartInfo cartInfo = cartFeignClient.addCart(skuId,skuNum);
        //url将页面参数传递
        return "redirect:http://cart.gmall.com:8300/addCart.html?skuName"+cartInfo.getSkuName()+"&skuDefaultImg="+cartInfo.getImgUrl();
    }
    //购物车复选框
    @RequestMapping("cart/cart.html")
    public String checkCart(){
        return "cart/index";
    }

}
