package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Date 2021/5/19 14:39
 * @Author JINdc
 **/
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public Map<String, Object> item(Long skuId) {
        Map<String, Object> map = new HashMap<>();

        //调用service-product查询基础数据
        SkuInfo skuInfo = productFeignClient.getSkuById(skuId);
        //查询图片列表
        List<SkuImage> skuImageList = productFeignClient.getSkuImageBySkuId(skuId);
        skuInfo.setSkuImageList(skuImageList);
        map.put("skuInfo",skuInfo);
        //查询商品价格
        BigDecimal price = productFeignClient.getSkuPriceById(skuId);
        map.put("price",price);
        //商品分类
        BaseCategoryView categoryView =productFeignClient.getCategoryViewByC3Id(skuInfo.getCategory3Id());
        map.put("categoryView",categoryView);

        //销售属性列表
        List<SpuSaleAttr> spuSaleAttrList = productFeignClient.getSpuSaleAttrListCheckBySku(skuInfo.getSpuId(),skuInfo.getId());
        map.put("spuSaleAttrList",spuSaleAttrList);
        return map;
    }
}
