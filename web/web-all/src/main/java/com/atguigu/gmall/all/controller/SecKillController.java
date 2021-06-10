package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.client.SeckillFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Date 2021/6/9 8:40
 * @Author JINdc
 **/
@Controller
public class SecKillController {

    @Autowired
    SeckillFeignClient seckillFeignClient;

    @RequestMapping("seckill/{skuId}")
    public String secKillHtml(Model model,@PathVariable("skuId") Long skuId){
        SeckillGoods seckillGoods =  seckillFeignClient.findBySkuId(skuId);
        model.addAttribute("item",seckillGoods);
        return "seckill/item";
    }

    @RequestMapping("seckill.html")
    public String secKillHtml(Model model){
        List<SeckillGoods> seckillGoodsList =  seckillFeignClient.findAll();
        model.addAttribute("list",seckillGoodsList);
        return "seckill/index";
    }
}
