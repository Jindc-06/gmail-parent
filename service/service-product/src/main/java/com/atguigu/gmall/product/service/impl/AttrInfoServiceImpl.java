package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.AttrInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Date 2021/5/17 19:17
 * @Author JINdc
 **/
@Service
public class AttrInfoServiceImpl implements AttrInfoService {

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Override
    public List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id) {
        QueryWrapper<BaseAttrInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id",category3Id);
        wrapper.eq("category_level",3);
        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.selectList(wrapper);

        for (BaseAttrInfo baseAttrInfo : baseAttrInfos) {
            Long attrInfoId = baseAttrInfo.getId();
            QueryWrapper<BaseAttrValue> valueQueryWrapper = new QueryWrapper<>();
            valueQueryWrapper.eq("attr_id",attrInfoId);
            List<BaseAttrValue> attrValueList = baseAttrValueMapper.selectList(valueQueryWrapper);
            baseAttrInfo.setAttrValueList(attrValueList);
        }
        return baseAttrInfos;
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        Long id = baseAttrInfo.getId();
        //判断是否有id
        if (id==null || id<0){
            //保存
            baseAttrInfoMapper.insert(baseAttrInfo);
            Long attrId = baseAttrInfo.getId();
            id = attrId;

        }else {
            //修改
            baseAttrInfoMapper.updateById(baseAttrInfo);

            //把修改的属性值删除
            QueryWrapper<BaseAttrValue> valueWrapper = new QueryWrapper<>();
            valueWrapper.eq("attr_id",id);
            baseAttrValueMapper.delete(valueWrapper);
        }
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        //添加属性值
        for (BaseAttrValue baseAttrValue : attrValueList) {
            baseAttrValue.setAttrId(id);
            baseAttrValueMapper.insert(baseAttrValue);
        }

    }

    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_id",attrId);
        List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.selectList(wrapper);
        return baseAttrValueList;
    }
}
