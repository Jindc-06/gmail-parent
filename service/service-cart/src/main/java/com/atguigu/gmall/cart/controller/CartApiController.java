package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.util.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Date 2021/5/31 14:05
 * @Author JINdc
 **/
@RestController
@RequestMapping("api/cart")
//@CrossOrigin
public class CartApiController {

    @Autowired
    private CartService cartService;
    //添加购物车
    @RequestMapping("addCart/{skuId}/{skuNum}")
    CartInfo addCart(@PathVariable("skuId") Long skuId, @PathVariable("skuNum") Long skuNum){
        //通过单点登录获取用户userId
        String userId = "1";
        CartInfo cartInfo = cartService.addCart(skuId,skuNum,userId);
        return cartInfo;
    }

    //购物车列表
    @RequestMapping("cartList")
    public Result cartList(){
        //通过单点登录获取用户userId
        String userId = "1";
        List<CartInfo> cartInfoList = cartService.cartList(userId);
        return Result.ok(cartInfoList);
    }

    //购物车复选框
    @RequestMapping("checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable("skuId")Long skuId, @PathVariable("isChecked")Integer isChecked){
        //通过单点登录获取用户userId
        String userId = "1";
        cartService.checkCart(skuId,isChecked,userId);
        return Result.ok();
    }
}
