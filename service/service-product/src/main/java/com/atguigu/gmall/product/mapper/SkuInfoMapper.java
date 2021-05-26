package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Date 2021/5/18 15:11
 * @Author JINdc
 **/
@Mapper
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    Goods selectGoodsBySkuId(@Param("skuId") Long skuId);
}
