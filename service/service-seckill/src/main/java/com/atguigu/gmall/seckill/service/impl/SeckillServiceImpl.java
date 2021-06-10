package com.atguigu.gmall.seckill.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.user.UserRecode;
import com.atguigu.gmall.rabbit.service.RabbitService;
import com.atguigu.gmall.seckill.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.seckill.service.SeckillService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Date 2021/6/9 9:01
 * @Author JINdc
 **/
@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RabbitService rabbitService;
    @Override
    public SeckillGoods findBySkuId(Long skuId) {
        QueryWrapper<SeckillGoods> wrapper = new QueryWrapper<>();
        wrapper.eq("sku_id",skuId);
        SeckillGoods seckillGoods = seckillGoodsMapper.selectOne(wrapper);
        return seckillGoods;
    }

    @Override
    public List<SeckillGoods> findAll() {
        List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.selectList(null);
        return seckillGoodsList;
    }

    @Override
    public void putSeckillGoods(Long skuId) {
        //数据库查询
        QueryWrapper<SeckillGoods> wrapper = new QueryWrapper<>();
        wrapper.eq("sku_id",skuId);
        SeckillGoods seckillGoods = seckillGoodsMapper.selectOne(wrapper);
        //放入redis
        if (seckillGoods!=null){
            //库存数量
            Integer stockCount = seckillGoods.getStockCount();
            for (Integer i = 0; i < stockCount; i++) {
                //将单个商品的库存存入单独的一个key,秒杀所有商品的库存为一个集合
                redisTemplate.opsForList().leftPush(RedisConst.SECKILL_STOCK_PREFIX +skuId,skuId);
            }
            redisTemplate.opsForHash().put(RedisConst.SECKILL_GOODS,skuId+"",seckillGoods);
        }
        //发送广播通知(1 表示有货 0 表示无货)
        redisTemplate.convertAndSend("seckillpush",skuId+":1");
    }

    @Override
    public void seckillOrder(String userId, Long skuId) {
        UserRecode userRecode = new UserRecode();
        userRecode.setSkuId(skuId);
        userRecode.setUserId(userId);
        //发送抢购消息队列
    }
}
