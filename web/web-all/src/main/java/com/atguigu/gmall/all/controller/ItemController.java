package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.item.client.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @Date 2021/5/19 14:34
 * @Author JINdc
 **/
@Controller
public class ItemController {

    @Autowired
    private ItemFeignClient itemFeignClient;

    @RequestMapping("{skuId}.html")
    public String index(Model model, @PathVariable("skuId")Long skuId){
        Map<String,Object> map = itemFeignClient.item(skuId);
        model.addAllAttributes(map);
        return "item/index";
    }


}
