package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Date 2021/5/31 14:07
 * @Author JINdc
 **/
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    private CartInfoMapper cartInfoMapper;

    @Override
    public CartInfo addCart(Long skuId, Long skuNum, String userId) {
        //feign调用,通过skuId获取skuInfo
        SkuInfo skuInfoById = productFeignClient.getSkuById(skuId);
        //为购物车信息赋值
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        cartInfo.setSkuId(skuId);
        cartInfo.setImgUrl(skuInfoById.getSkuDefaultImg());
        cartInfo.setSkuName(skuInfoById.getSkuName());
        //商品上架时的价格,会经常改变 ,不加入数据库
        //cartInfo.setSkuPrice();
        cartInfo.setSkuNum(skuNum.intValue());
        cartInfo.setCartPrice(skuInfoById.getPrice().multiply(new BigDecimal(skuNum)));
        //判断redis缓存是否有该购物车信息(用一个hash储存)
        CartInfo cartInfoCache = (CartInfo) redisTemplate.opsForHash().get("user:" + userId + ":cart", skuId + "");
        //数据库备份购物车信息
        if (cartInfoCache ==null){
            //将数据插入mysql
            cartInfoMapper.insert(cartInfo);
        }else {
            //更新mysql(加入数据库操作,为改变价格cartPrice,skuNum)
            QueryWrapper<CartInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id",userId);
            wrapper.eq("sku_id",skuId);
            cartInfo.setSkuNum(cartInfoCache.getSkuNum()+skuNum.intValue());
            cartInfo.setCartPrice(skuInfoById.getPrice().multiply(new BigDecimal(cartInfo.getSkuNum())));
            cartInfoMapper.update(cartInfo,wrapper);
        }
        //redis缓存有当前skuPrice
        cartInfo.setSkuPrice(skuInfoById.getPrice());
        //将购物车信息放入redis缓存
        redisTemplate.opsForHash().put("user:" + userId + ":cart", skuId + "",cartInfo);
        return cartInfo;
    }

    @Override
    public List<CartInfo> cartList(String userId) {
        //先查询缓存中是否有数据
        List<CartInfo> cartInfoList = redisTemplate.opsForHash().values("user:" + userId + ":cart");
        //如果缓存无数据(数据库备份的数据)
        if(cartInfoList == null && cartInfoList.size()<=0){
            QueryWrapper<CartInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id",userId);
            List<CartInfo> cartInfoListDB = cartInfoMapper.selectList(wrapper);
            //DB查询到的数据存入缓存
            if(cartInfoListDB!=null&&cartInfoListDB.size()>0){
                Map<String, Object> cartInfoMap = new HashMap<>();
                for (CartInfo cartInfoDB : cartInfoListDB) {
                    //数据库中没有skuPrice(购物车列表 价格是单独查询)
                    cartInfoDB.setSkuPrice(productFeignClient.getSkuPriceById(cartInfoDB.getSkuId()));
                    cartInfoMap.put(cartInfoDB.getSkuId()+"",cartInfoDB);
                }
                redisTemplate.opsForHash().putAll("user:" + userId + ":cart",cartInfoMap);
            }
        }
        return cartInfoList;
    }

    @Override
    public void checkCart(Long skuId, Integer isChecked, String userId) {
        //先查询缓存
        CartInfo cartInfo = (CartInfo) redisTemplate.opsForHash().get("user:" + userId + ":cart", skuId+"");
        Long cartInfoId = cartInfo.getId();
        cartInfo.setIsChecked(isChecked);
        //将修改后的isChecked 放入缓存
        redisTemplate.opsForHash().put("user:" + userId + ":cart", skuId + "",cartInfo);

        //数据库同步状态()
        cartInfoMapper.updateById(cartInfo);
    }
}
