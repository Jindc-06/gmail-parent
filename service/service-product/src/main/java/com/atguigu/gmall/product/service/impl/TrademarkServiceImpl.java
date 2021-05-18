package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.TrademarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Date 2021/5/17 20:25
 * @Author JINdc
 **/
@Service
public class TrademarkServiceImpl implements TrademarkService {

    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;

    @Override
    public IPage<BaseTrademark> baseTrademark(Long page, Long limit) {
        IPage<BaseTrademark> iPage = new Page<>(page,limit);
        IPage<BaseTrademark> baseTrademarkIPage = baseTrademarkMapper.selectPage(iPage,null);
        return baseTrademarkIPage;
    }

    @Override
    public List<BaseTrademark> getTrademarkList() {
        List<BaseTrademark> baseTrademarkList = baseTrademarkMapper.selectList(null);
        return baseTrademarkList;
    }
}
