package com.atguigu.gmall.search.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @Date 2021/5/26 11:40
 * @Author JINdc
 **/
public interface SearchService {
    List<JSONObject> getCategoryToIndex();

    void onSale(Long skuId);

    void cancelSale(Long skuId);
}
