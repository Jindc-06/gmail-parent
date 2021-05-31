package com.atguigu.gmall.search.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import com.atguigu.gmall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Date 2021/5/26 11:36
 * @Author JINdc
 **/
@RestController
@RequestMapping("api/search")
public class SearchApiController {

    @Autowired
    private SearchService searchService;

    //首页三级分类属性列表
    @RequestMapping("getCategoryToIndex")
    List<JSONObject> getCategoryToIndex(){
        List<JSONObject> jsonObjects =  searchService.getCategoryToIndex();
        return jsonObjects;
    }

    //商品上架
    @RequestMapping("onSale/{skuId}")
    void onSale(@PathVariable("skuId") Long skuId){
        searchService.onSale(skuId);
    }

    //商品下架
    @RequestMapping("cancelSale/{skuId}")
    void cancelSale(@PathVariable("skuId")Long skuId){
        searchService.cancelSale(skuId);
    }

    //es,搜索框或商品属性搜索
    @RequestMapping("list")
    SearchResponseVo list(@RequestBody SearchParam searchParam){
        SearchResponseVo searchResponseVo = searchService.list(searchParam);
        return searchResponseVo;
    }

    //商品详情页(item)更新热度值
    @RequestMapping("hotScore/{skuId}")
    void hotScore(@PathVariable("skuId")Long skuId){
        searchService.hotScore(skuId);
    }
}
