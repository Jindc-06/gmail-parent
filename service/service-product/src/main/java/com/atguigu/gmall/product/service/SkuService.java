package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @Date 2021/5/18 15:08
 * @Author JINdc
 **/
public interface SkuService {
    void saveSkuInfo(SkuInfo skuInfo);

    IPage<SkuInfo> skuList(Long page, Long limit);

    void onSale(Long skuId);

    void cancelSale(Long skuId);
}
