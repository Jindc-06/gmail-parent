package com.atguigu.gmall.product.comtroller;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.CategoryService;
import com.atguigu.gmall.product.service.SkuService;
import com.atguigu.gmall.product.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Date 2021/5/19 14:40
 * @Author JINdc
 **/
@RestController
@RequestMapping("api/product/")
@CrossOrigin
public class ProductApiController {

    @Autowired
    private SkuService skuService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SpuService spuService;

    //查询sku基础数据
    @RequestMapping("getSkuById/{skuId}")
    SkuInfo getSkuById(@PathVariable("skuId") Long skuId){
        SkuInfo skuInfo = skuService.getSkuById(skuId);
        return skuInfo;
    }
    //查询sku图片
    @RequestMapping("getSkuImageBySkuId/{skuId}")
    List<SkuImage> getSkuImageBySkuId(@PathVariable("skuId") Long skuId){
        List<SkuImage> skuImageList = skuService.getSkuImageBySkuId(skuId);
        return skuImageList;
    }
    //查询sku价格
    @RequestMapping("getSkuPriceById/{skuId}")
    BigDecimal getSkuPriceById(@PathVariable("skuId")Long skuId){
        BigDecimal price = skuService.getSkuPriceById(skuId);
        return price;
    }

    //通过三级分类查询商品分类信息
    @RequestMapping("getCategoryViewByC3Id/{category3Id}")
    BaseCategoryView getCategoryViewByC3Id(@PathVariable("category3Id")Long category3Id){

        BaseCategoryView categoryView = categoryService.getCategoryViewByC3Id(category3Id);
        return categoryView;
    }

    //销售属性列表
    @RequestMapping("getSpuSaleAttrListCheckBySku/{spuId}/{skuId}")
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("spuId")Long spuId,
                                                   @PathVariable("skuId")Long skuId){

        List<SpuSaleAttr> spuSaleAttrList = spuService.getSpuSaleAttrListCheckBySku(spuId,skuId);
        return spuSaleAttrList;
    }

    //切换点击销售属性对应得list ,dao层查询返回一个集合
    @RequestMapping("getSaleAttrValuesBySku/{spuId}")
    List<Map<String, Object>> getSaleAttrValuesBySku(@PathVariable("spuId")Long spuId){

        List<Map<String, Object>> valuesSkuBySpuList = skuService.getSaleAttrValuesBySku(spuId);
        return valuesSkuBySpuList;
    }
    //首页三级分类属性列表
    @RequestMapping("getCategoryToIndex")
    List<BaseCategoryView> getCategoryToIndex(){
        List<BaseCategoryView> categoryViews = categoryService.getCategoryToIndex();
        return categoryViews;
    }
    //商品上架
    @RequestMapping("getGoodsBySkuId/{skuId}")
    Goods getGoodsBySkuId(@PathVariable("skuId")Long skuId){
        Goods goods = skuService.getGoodsBySkuId(skuId);
        return goods;
    }

}
