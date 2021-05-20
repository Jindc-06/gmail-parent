package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.product.mapper.SkuImageMapper;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.mapper.SkuSaleAttrValueMapper;
import com.atguigu.gmall.product.service.SkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Date 2021/5/18 15:09
 * @Author JINdc
 **/
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //保存sku信息
        skuInfoMapper.insert(skuInfo);
        //获取主键
        Long skuId = skuInfo.getId();

        //保存图片
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (skuImageList != null && skuImageList.size()>0){
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuId);
                skuImageMapper.insert(skuImage);
            }
        }
        //保存平台属性值
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (skuAttrValueList !=null && skuAttrValueList.size()>0){
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuId);
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }

        //保存销售属性值
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (skuSaleAttrValueList !=null && skuSaleAttrValueList.size()>0){
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuId);
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }
    }

    @Override
    public IPage<SkuInfo> skuList(Long page, Long limit) {

        IPage<SkuInfo> iPage = new Page<>(page,limit);
        IPage<SkuInfo> skuInfoIPage = skuInfoMapper.selectPage(iPage, null);
        return skuInfoIPage;
    }

    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);
    }

    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);
    }

    @Override
    public SkuInfo getSkuById(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo;
    }

    @Override
    public List<SkuImage> getSkuImageBySkuId(Long skuId) {
        QueryWrapper<SkuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("sku_id",skuId);
        List<SkuImage> skuImageList = skuImageMapper.selectList(wrapper);
        return skuImageList;
    }

    @Override
    public BigDecimal getSkuPriceById(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo.getPrice();
    }
}
