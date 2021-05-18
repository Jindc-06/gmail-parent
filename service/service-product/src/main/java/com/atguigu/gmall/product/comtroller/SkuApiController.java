package com.atguigu.gmall.product.comtroller;

import com.atguigu.gmall.common.util.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.SkuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Date 2021/5/18 15:07
 * @Author JINdc
 **/
@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class SkuApiController {

    @Autowired
    private SkuService skuService;

    //添加sku
    @RequestMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){

        skuService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    //获取sku分页列表
    @RequestMapping("list/{page}/{limit}")
    public Result skuList(@PathVariable("page") Long page,
                          @PathVariable("limit") Long limit){

        IPage<SkuInfo> skuInfoIPage = skuService.skuList(page,limit);
        return Result.ok(skuInfoIPage);
    }
    //上架
    @RequestMapping("onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId){

        skuService.onSale(skuId);
        return Result.ok();
    }
    //下架
    @RequestMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId){

        skuService.cancelSale(skuId);
        return Result.ok();
    }


}
