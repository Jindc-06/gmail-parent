package com.atguigu.gmall.seckill.client;

import com.atguigu.gmall.model.activity.SeckillGoods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Date 2021/6/9 8:45
 * @Author JINdc
 **/
@FeignClient(value = "service-activity")
public interface SeckillFeignClient {
    //秒杀详情页面
    @RequestMapping("api/activity/seckill/findBySkuId/{skuId}")
    SeckillGoods findBySkuId(@PathVariable("skuId") Long skuId);
    //秒杀页面集合
    @RequestMapping("api/activity/seckill/findAll")
    List<SeckillGoods> findAll();
}
