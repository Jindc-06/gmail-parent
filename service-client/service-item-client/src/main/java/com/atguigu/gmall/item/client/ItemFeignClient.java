package com.atguigu.gmall.item.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @Date 2021/5/19 14:23
 * @Author JINdc
 **/
@FeignClient(value = "service-item")
public interface ItemFeignClient {

    @RequestMapping("api/item/{skuId}")
    public Map<String, Object> item(@PathVariable("skuId") Long skuId);
}
