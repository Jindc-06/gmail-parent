package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Date 2021/5/17 21:28
 * @Author JINdc
 **/
@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(@Param("spuId") Long spuId,@Param("skuId") Long skuId);
}
