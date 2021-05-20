package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @Date 2021/5/17 20:47
 * @Author JINdc
 **/
public interface SpuService {

    List<BaseSaleAttr> baseSaleAttrList();

    void saveSpuInfo(SpuInfo spuInfo);

    IPage<SpuInfo> getSpuList(Long page, Long limit,Long category3Id);

    List<SpuImage> spuImageList(Long spuId);

    List<SpuSaleAttr> spuSaleAttrList(Long spuId);

    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long spuId, Long skuId);
}
