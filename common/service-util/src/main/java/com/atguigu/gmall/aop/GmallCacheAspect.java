package com.atguigu.gmall.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Date 2021/5/22 14:51
 * @Author JINdc
 **/
@Component
@Aspect
public class GmallCacheAspect {


    @Autowired
    private RedisTemplate redisTemplate;

    @Around("@annotation(com.atguigu.gmall.aop.GmallCache)")
    public Object gCache(ProceedingJoinPoint proceedingJoinPoint){

        Object result = null;

        //调用代理对象的反射信息(skuInfo)
        MethodSignature ms = (MethodSignature) proceedingJoinPoint.getSignature();
        //注解信息
        GmallCache gmallCache = ms.getMethod().getAnnotation(GmallCache.class);
        //方法名
        String name = ms.getMethod().getName();
        //返回类型
        Class returnType = ms.getReturnType();
        //参数
        Object[] args = proceedingJoinPoint.getArgs();
        //作为redis的key,设置唯一key,用方法名+参数拼接(注意)
        String redisKey = name;
        //获取前缀后缀
        if(args!= null && args.length>0){
            for (Object arg : args) {
                redisKey = redisKey +":"+arg;
            }
        }

        //查询缓存
       result = redisTemplate.opsForValue().get(redisKey);
        //如果没有缓存,则查询数据库
        if (result == null) {
            //设置redis锁的值
            String lockTag = UUID.randomUUID().toString();
            //设置判断redis是否有lockTag的值 ,不存在可以操作 返回true
            Boolean ifLock = redisTemplate.opsForValue().setIfAbsent(redisKey + ":lock", lockTag, 1, TimeUnit.SECONDS);

            if (ifLock) {//redis缓存中不存在lockTag,则调用代理方法,查询数据库
                try {
                    //调用被代理方法
                    result = proceedingJoinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                //判断查询数据库是否有值 ,防止缓存穿透现象 ,用户绕过缓存直接访问数据库(网页访问无效的url)
                if (result != null) {
                    //当result有值,得到数据库数据同步到redis缓存
                    redisTemplate.opsForValue().set(redisKey, result);

                } else {//当result没值
                    Object instance = null;
                    try {
                        //创建一个新实例
                        instance = returnType.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    //设置一个空值放进缓存,防止缓存穿透
                    redisTemplate.opsForValue().set(redisKey, instance, 10, TimeUnit.SECONDS);
                }
                //归还同步锁，判断当前的锁值是否是创建时的锁值
//                String currentLockTag = (String)redisTemplate.opsForValue().get("Sku:" + skuId + ":lock");
//                if(!StringUtils.isEmpty(currentLockTag)&&currentLockTag.equals(lockTag)){
//                    redisTemplate.delete("Sku:" + skuId + ":lock");
//                }
                //lua脚本(归还同步锁,将得到锁和删除锁设置原子性)
                String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                //设置脚本类型
                redisScript.setResultType(Long.class);
                //设置脚本的文本为lua脚本
                redisScript.setScriptText(luaScript);
                //redis执行lua脚本,设置锁的key(为一个集合),value
                redisTemplate.execute(redisScript, Arrays.asList(redisKey + ":lock"), lockTag);
            } else {
                try {
                    //未拿到锁等待1秒,重新访问自己
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //自旋
                return redisTemplate.opsForValue().get(redisKey);
                //return gCache(proceedingJoinPoint);
            }
        }
        //有缓存直接返回数据

        return result;
    }
}
