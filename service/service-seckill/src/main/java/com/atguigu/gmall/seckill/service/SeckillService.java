package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.model.activity.SeckillGoods;

import java.util.List;

/**
 * @Date 2021/6/9 9:01
 * @Author JINdc
 **/
public interface SeckillService {
    SeckillGoods findBySkuId(Long skuId);

    List<SeckillGoods> findAll();

    void putSeckillGoods(Long skuId);

    void seckillOrder(String userId, Long skuId);
}
