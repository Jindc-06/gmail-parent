package com.atguigu.gmall.product.client;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Date 2021/5/19 14:23
 * @Author JINdc
 **/
@FeignClient(value = "service-product")
public interface ProductFeignClient {

    //查询sku基础数据
    @RequestMapping("api/product/getSkuById/{skuId}")
    SkuInfo getSkuById(@PathVariable("skuId") Long skuId);

    //查询sku图片
    @RequestMapping("api/product/getSkuImageBySkuId/{skuId}")
    List<SkuImage> getSkuImageBySkuId(@PathVariable("skuId") Long skuId);

    //查询sku价格
    @RequestMapping("api/product/getSkuPriceById/{skuId}")
    BigDecimal getSkuPriceById(@PathVariable("skuId")Long skuId);

    //通过三级分类查询商品分类信息
    @RequestMapping("api/product/getCategoryViewByC3Id/{category3Id}")
    BaseCategoryView getCategoryViewByC3Id(@PathVariable("category3Id")Long category3Id);

    //销售属性列表
    @RequestMapping("api/product/getSpuSaleAttrListCheckBySku/{spuId}/{skuId}")
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("spuId")Long spuId,
                                                   @PathVariable("skuId")Long skuId);
}
