package com.atguigu.gmall.cart.client;

import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Date 2021/5/31 14:00
 * @Author JINdc
 **/
@FeignClient(value = "service-cart")
public interface CartFeignClient {

    //添加购物车
    @RequestMapping("api/cart/addCart/{skuId}/{skuNum}")
    CartInfo addCart(@PathVariable("skuId") Long skuId,@PathVariable("skuNum") Long skuNum);
}
