package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Date 2021/5/18 15:08
 * @Author JINdc
 **/
public interface SkuService {
    void saveSkuInfo(SkuInfo skuInfo);

    IPage<SkuInfo> skuList(Long page, Long limit);

    void onSale(Long skuId);

    void cancelSale(Long skuId);

    SkuInfo getSkuById(Long skuId);

    List<SkuImage> getSkuImageBySkuId(Long skuId);

    BigDecimal getSkuPriceById(Long skuId);

    List<Map<String, Object>> getSaleAttrValuesBySku(Long spuId);

    Goods getGoodsBySkuId(Long skuId);
}
