package com.atguigu.gmall.product.comtroller;

import com.atguigu.gmall.common.util.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.TrademarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Date 2021/5/17 20:22
 * @Author JINdc
 **/
@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class TrademarkApiController {

    @Autowired
    private TrademarkService trademarkService;
    //品牌分页
    @RequestMapping("baseTrademark/{page}/{limit}")
    public Result baseTrademark(@PathVariable("page")Long page,
                                @PathVariable("limit")Long limit){

        IPage<BaseTrademark> baseTrademarkIPage = trademarkService.baseTrademark(page,limit);
        return Result.ok(baseTrademarkIPage);
    }

    //获取品牌属性
    @RequestMapping("baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        List<BaseTrademark> baseTrademarkList = trademarkService.getTrademarkList();
        return Result.ok(baseTrademarkList);
    }
}
