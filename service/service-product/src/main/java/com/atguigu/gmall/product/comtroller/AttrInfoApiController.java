package com.atguigu.gmall.product.comtroller;

import com.atguigu.gmall.common.util.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.AttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Date 2021/5/17 19:15
 * @Author JINdc
 **/
@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class AttrInfoApiController {

    @Autowired
    private AttrInfoService attrInfoService;

    //平台属性回显
    @RequestMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable("category1Id") Long category1Id,
                               @PathVariable("category2Id") Long category2Id,
                               @PathVariable("category3Id") Long category3Id){

        List<BaseAttrInfo> baseAttrInfoList =  attrInfoService.attrInfoList(category1Id,category2Id,category3Id);
        return Result.ok(baseAttrInfoList);
    }

    //添加平台属性
    @RequestMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){

        attrInfoService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }
    //根据平台属性ID获取平台属性
    @RequestMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable Long attrId){

        List<BaseAttrValue> baseAttrValueList = attrInfoService.getAttrValueList(attrId);
        return Result.ok(baseAttrValueList);
    }


}
