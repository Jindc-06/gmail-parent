package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @Date 2021/5/17 20:25
 * @Author JINdc
 **/
public interface TrademarkService {
    IPage<BaseTrademark> baseTrademark(Long page, Long limit);

    List<BaseTrademark> getTrademarkList();
}
