package com.atguigu.gmall.search.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Date 2021/5/26 11:40
 * @Author JINdc
 **/
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ProductFeignClient productFeignClient;

//    @Autowired
//    ElasticsearchRestTemplate restTemplate;

    @Autowired
    GoodsRepository goodsRepository;

    @Override
    public List<JSONObject> getCategoryToIndex() {
        List<BaseCategoryView> categoryViews = productFeignClient.getCategoryToIndex();

        //一级分类属性集合
        List<JSONObject> c1JSONObjectList = new ArrayList<>();
        //处理后台查询得到的分类属性集合,通过Category1Id得到c1map元素
        Map<Long, List<BaseCategoryView>> c1Map = categoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        for (Map.Entry<Long, List<BaseCategoryView>> c1ObjectMap : c1Map.entrySet()) {
            //一级分类的元素(json对象本质是一个map集合)
            JSONObject c1JsonObject = new JSONObject();
            Long c1Id = c1ObjectMap.getKey();
            String category1Name = c1ObjectMap.getValue().get(0).getCategory1Name();
            c1JsonObject.put("categoryId",c1Id);
            c1JsonObject.put("categoryName",category1Name);

            //二级分类集合
            List<JSONObject> c2JSONObjectList = new ArrayList<>();
            //处理一级分类map元素 ,通过Category2Id得到
            Map<Long, List<BaseCategoryView>> c2Map = c1ObjectMap.getValue().stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            for (Map.Entry<Long, List<BaseCategoryView>> c2ObjectMap : c2Map.entrySet()) {
                //二级分类元素
                JSONObject c2JsonObject = new JSONObject();
                Long c2Id = c2ObjectMap.getKey();
                String category2Name = c2ObjectMap.getValue().get(0).getCategory2Name();
                c2JsonObject.put("categoryId",c2Id);
                c2JsonObject.put("categoryName",category2Name);

                //三级分类集合
                List<JSONObject> c3JSONObjectList = new ArrayList<>();
                //处理二级分类map元素 ,通过Category3Id得到
                Map<Long, List<BaseCategoryView>> c3Map = c2ObjectMap.getValue().stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id));
                for (Map.Entry<Long, List<BaseCategoryView>> c3ObjectMap : c3Map.entrySet()) {
                    //三级分类元素
                    JSONObject c3JsonObject = new JSONObject();
                    Long c3Id = c3ObjectMap.getKey();
                    String category3Name = c3ObjectMap.getValue().get(0).getCategory3Name();
                    c3JsonObject.put("categoryId",c3Id);
                    c3JsonObject.put("categoryName",category3Name);
                    c3JSONObjectList.add(c3JsonObject);
                }
                //三级分类集合加入二级分类元素,作为child
                c2JsonObject.put("categoryChild",c3JSONObjectList);
                //将二级分类元素c2JsonObject添加到二级集合
                c2JSONObjectList.add(c2JsonObject);
            }
            //二级分类集合加入一级分类元素,作为child
            c1JsonObject.put("categoryChild",c2JSONObjectList);
            //将一级分类元素c1JsonObject添加到一级集合
            c1JSONObjectList.add(c1JsonObject);
        }
        return c1JSONObjectList;
    }

    @Override
    public void onSale(Long skuId) {
        Goods goods = null;
        //从数据库中查询,存入es中
        goods = productFeignClient.getGoodsBySkuId(skuId);
        goods.setCreateTime(new Date());
        goodsRepository.save(goods);
    }

    @Override
    public void cancelSale(Long skuId) {
        //下架删除
        Goods goods = new Goods();
        goods.setId(skuId);
        goodsRepository.delete(goods);
    }
}
