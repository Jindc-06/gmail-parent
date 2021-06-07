package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;

/**
 * @Date 2021/5/31 14:07
 * @Author JINdc
 **/
public interface CartService {
    CartInfo addCart(Long skuId, Long skuNum, String userId);

    List<CartInfo> cartList(String userId);

    void checkCart(Long skuId, Integer isChecked, String userId);

    List<CartInfo> getTradeOrder(String userId);
}
