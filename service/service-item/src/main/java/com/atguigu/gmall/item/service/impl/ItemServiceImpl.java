package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.atguigu.gmall.search.client.SearchFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @Date 2021/5/19 14:39
 * @Author JINdc
 **/
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    SearchFeignClient searchFeignClient;
    @Override
    public Map<String, Object> item(Long skuId) {
        long start = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();

        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                //调用service-product查询skuInfo基础数据
                SkuInfo skuInfo = productFeignClient.getSkuById(skuId);
                return skuInfo;
            }
        },threadPoolExecutor);

        CompletableFuture<Void> imgCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                //查询图片列表
                List<SkuImage> skuImageList = productFeignClient.getSkuImageBySkuId(skuId);
                skuInfo.setSkuImageList(skuImageList);
                map.put("skuInfo",skuInfo);
            }
        },threadPoolExecutor);

        //单独一个线程查询
        CompletableFuture<Void> priceCompletableFuture = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                //查询商品价格
                BigDecimal price = productFeignClient.getSkuPriceById(skuId);
                map.put("price", price);
            }
        },threadPoolExecutor);

        CompletableFuture<Void> categoryCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                //商品分类
                BaseCategoryView categoryView =productFeignClient.getCategoryViewByC3Id(skuInfo.getCategory3Id());
                map.put("categoryView",categoryView);
            }
        },threadPoolExecutor);

        CompletableFuture<Void> spuSaleAttrCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                //销售属性列表集合
                List<SpuSaleAttr> spuSaleAttrList = productFeignClient.getSpuSaleAttrListCheckBySku(skuInfo.getSpuId(),skuInfo.getId());
                map.put("spuSaleAttrList",spuSaleAttrList);

                //切换点击销售属性对应得list
                //dao层查询返回得结果是一个集合
                List<Map<String,Object>> valuesSkuBySkuList = productFeignClient.getSaleAttrValuesBySku(skuInfo.getSpuId());
                //如果返回得结果不为空,则返回给前端
                if (valuesSkuBySkuList !=null && valuesSkuBySkuList.size()>0){
                    Map<String,Object> valueSkuMap = new HashMap<>();
                    //遍历查询到得集合
                    for (Map<String, Object> valuesSkuBySku : valuesSkuBySkuList) {
                        //前台收到的值  (22|23:6,44|15:6)
                        String valueId = (String) valuesSkuBySku.get("valueId");
                        Integer skuIdForValue = (Integer) valuesSkuBySku.get("sku_id");
                        valueSkuMap.put(valueId,skuIdForValue);
                    }
                    String valueSkuJson = JSON.toJSONString(valueSkuMap);
                    map.put("valuesSkuJson",valueSkuJson);
                }
            }
        },threadPoolExecutor);
        CompletableFuture.allOf(skuInfoCompletableFuture,
                                imgCompletableFuture,
                                priceCompletableFuture,
                                categoryCompletableFuture,
                                spuSaleAttrCompletableFuture
                                ).join();
        long end = System.currentTimeMillis();
        System.out.println("执行时间:-->"+(end-start));
        //调用search搜索服务器,更新热度值
        searchFeignClient.hotScore(skuId);
        return map;
    }
}
