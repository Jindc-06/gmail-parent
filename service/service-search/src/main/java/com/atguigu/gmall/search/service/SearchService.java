package com.atguigu.gmall.search.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.list.SearchResponseVo;

import java.util.List;

/**
 * @Date 2021/5/26 11:40
 * @Author JINdc
 **/
public interface SearchService {
    List<JSONObject> getCategoryToIndex();

    void onSale(Long skuId);

    void cancelSale(Long skuId);

    SearchResponseVo list(SearchParam searchParam);

    void hotScore(Long skuId);
}
