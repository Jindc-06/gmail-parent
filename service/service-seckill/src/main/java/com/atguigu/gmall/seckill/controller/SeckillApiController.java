package com.atguigu.gmall.seckill.controller;

import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.common.util.Result;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.config.CacheHelper;
import com.atguigu.gmall.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Date 2021/6/9 9:00
 * @Author JINdc
 **/
@RestController
@RequestMapping("api/activity/seckill")
public class SeckillApiController {

    @Autowired
    SeckillService seckillService;
    //秒杀详情页面
    @RequestMapping("findBySkuId/{skuId}")
    SeckillGoods findBySkuId(@PathVariable("skuId") Long skuId){
        SeckillGoods seckillGoods = seckillService.findBySkuId(skuId);
        return seckillGoods;
    }
    //秒杀页面集合
    @RequestMapping("findAll")
    List<SeckillGoods> findAll(){
        List<SeckillGoods> seckillGoodsList = seckillService.findAll();
        return seckillGoodsList;
    }
    //发布秒杀的商品信息
    @RequestMapping("putSeckillGoods/{skuId}")
    public Result putSeckillGoods(@PathVariable("skuId")Long skuId){
        seckillService.putSeckillGoods(skuId);
        return Result.ok();
    }

    //获取获取缓存中的秒杀商品的库存信息
    @RequestMapping("getStatus/{skuId}")
    public Result getStatus(@PathVariable("skuId")Long skuId){
        return Result.ok(CacheHelper.get("sku:"+skuId));
    }
    //点击抢购 , 生成一个抢购码,防止多次请求,一个用户多个ip一起抢购
    @RequestMapping("auth/getSeckillSkuIdStr/{skuId}")
    public Result getSeckillSkuIdStr(@PathVariable("skuId")Long skuId, HttpServletRequest request){
        String userId = request.getHeader("userId");
        String encrypt = MD5.encrypt(userId + skuId);
        return Result.ok(encrypt);
    }
    //发送抢购队列
    @RequestMapping("auth/seckillOrder/{skuId}")
    public Result seckillOrder(@PathVariable("skuId")Long skuId, HttpServletRequest request){
        String userId = request.getHeader("userId");
        seckillService.seckillOrder(userId,skuId);
        return Result.ok();
    }
}
