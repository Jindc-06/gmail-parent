package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Date 2021/6/2 20:05
 * @Author JINdc
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    UserAddressMapper userAddressMapper;

    @Override
    public Map<String, Object> verify(String token) {
        Map<String, Object> verifyMap = new HashMap<>();
        //从redis中通过token验证 获取值
        UserInfo userInfo  = (UserInfo) redisTemplate.opsForValue().get("user:login" + token);
        if (userInfo!=null){
            verifyMap.put("success","success");
            verifyMap.put("userId",userInfo.getId()+"");
        }
        return verifyMap;
    }

    @Override
    public UserInfo login(UserInfo userInfo) {
        String loginName = userInfo.getLoginName();
        String passwd = userInfo.getPasswd();
        //将密码MD5加密
        String encryptPasswd = MD5.encrypt(passwd);
        //从数据库查询账号密码是否正确
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("login_name",loginName);
        wrapper.eq("passwd",encryptPasswd);
        UserInfo userInfoDB = userInfoMapper.selectOne(wrapper);
        if (userInfoDB != null){
            //登录成功
            String token = UUID.randomUUID().toString();
            //保存token
            redisTemplate.opsForValue().set("user:login:"+token,userInfoDB);
            userInfo.setToken(token);
            return userInfoDB;
        }else {
            return null;
        }
    }

    @Override
    public List<UserAddress> getUserAddresses(String userId) {
        QueryWrapper<UserAddress> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        List<UserAddress> userAddressList = userAddressMapper.selectList(wrapper);
        return userAddressList;
    }
}
