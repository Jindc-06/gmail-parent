package com.atguigu.gmall.search.client;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Date 2021/5/26 11:20
 * @Author JINdc
 **/
@FeignClient(value = "service-search")
public interface SearchFeignClient {

    //首页三级分类属性列表
    @RequestMapping("api/search/getCategoryToIndex")
    List<JSONObject> getCategoryToIndex();

    //商品上架
    @RequestMapping("api/search/onSale/{skuId}")
    void onSale(@PathVariable("skuId") Long skuId);

    //商品下架
    @RequestMapping("api/search/cancelSale/{skuId}")
    void cancelSale(@PathVariable("skuId")Long skuId);

    //es,搜索框或商品属性搜索
    @RequestMapping("api/search/list")
    SearchResponseVo list(@RequestBody SearchParam searchParam);

    //商品详情页(item)更新热度值
    @RequestMapping("api/search/hotScore/{skuId}")
    void hotScore(@PathVariable("skuId")Long skuId);
}
