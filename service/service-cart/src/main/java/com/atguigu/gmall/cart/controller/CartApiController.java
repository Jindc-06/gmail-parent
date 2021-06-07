package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.util.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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
    CartInfo addCart(HttpServletRequest request, @PathVariable("skuId") Long skuId, @PathVariable("skuNum") Long skuNum){
        //通过单点登录获取用户userId
        String userId = request.getHeader("userId");
        String userTempId = request.getHeader("userTempId");
        CartInfo cartInfo = cartService.addCart(skuId,skuNum,userId);
        return cartInfo;
    }

    //购物车列表
    @RequestMapping("cartList")
    public Result cartList(HttpServletRequest request){
        //通过单点登录获取用户userId
        String userId = request.getHeader("userId");
        String userTempId = request.getHeader("userTempId");
        List<CartInfo> cartInfoList = cartService.cartList(userId);
        return Result.ok(cartInfoList);
    }

    //购物车复选框
    @RequestMapping("checkCart/{skuId}/{isChecked}")
    public Result checkCart(HttpServletRequest request,@PathVariable("skuId")Long skuId, @PathVariable("isChecked")Integer isChecked){
        //通过单点登录获取用户userId
        String userId = request.getHeader("userId");
        String userTempId = request.getHeader("userTempId");
        cartService.checkCart(skuId,isChecked,userId);
        return Result.ok();
    }

    //订单调用,获取购物车信息
    @RequestMapping("getTradeOrder/{userId}")
    List<CartInfo> getTradeOrder(@PathVariable("userId")String userId){
        List<CartInfo> cartInfoList = cartService.getTradeOrder(userId);
        return cartInfoList;
    }
}
