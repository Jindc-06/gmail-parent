package com.atguigu.gmall.product.comtroller;

import com.atguigu.gmall.common.util.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.SpuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Date 2021/5/17 20:45
 * @Author JINdc
 **/
@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class SpuApiController {

    @Autowired
    private SpuService spuService;

    //获取销售属性
    @RequestMapping("baseSaleAttrList")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrList = spuService.baseSaleAttrList();
        return Result.ok(baseSaleAttrList);
    }

    //添加spu
    @RequestMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        spuService.saveSpuInfo(spuInfo);
        return Result.ok();
    }
    //获取spu分页列表
    @RequestMapping("{page}/{limit}")
    public Result getSpuList(@PathVariable("page")Long page,
                              @PathVariable("limit")Long limit,
                              Long category3Id){
        IPage<SpuInfo> spuInfoIPage = spuService.getSpuList(page,limit,category3Id);
        return Result.ok(spuInfoIPage);
    }

    //根据spuId获取图片列表
    @RequestMapping("spuImageList/{spuId}")
    public Result spuImageList(@PathVariable("spuId") Long spuId){
        List<SpuImage> spuImages = spuService.spuImageList(spuId);
        return Result.ok(spuImages);
    }

    //根据spuId获取销售属性
    @RequestMapping("spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable("spuId") Long spuId){
        List<SpuSaleAttr> spuSaleAttrs = spuService.spuSaleAttrList(spuId);
        return Result.ok(spuSaleAttrs);
    }
}
