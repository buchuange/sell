package com.atstar.sell.service;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.xml.core.Commit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by Dawn
 * 2017-08-07 23:55
 *
 * redis分布式锁： https://www.cnblogs.com/crazymakercircle/p/14731826.html
 *                http://www.redis.cn/commands/setnx.html
 */
@Component
@Slf4j
public class RedisLock {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 加锁
     * setnx + getset
     * @param key
     * @param value 当前时间+超时时间 150
     * @return
     */
    public boolean lock(String key, String value) {
        if (redisTemplate.opsForValue().setIfAbsent(key, value)) {
            return true;
        }

        // currentValue=A   这两个线程的value都是B  其中一个线程拿到锁

        String currentValue = redisTemplate.opsForValue().get(key);
        /*
            如果锁过期
            在业务处理过程中可能由于某种未知异常，导致死锁产生，下面是让死锁解锁的代码
         */
        if (StringUtils.hasLength(currentValue)
                && Long.parseLong(currentValue) < System.currentTimeMillis()) {
            // 获取上一个锁的时间
            String oldValue = redisTemplate.opsForValue().getAndSet(key, value);
            if (StringUtils.hasLength(oldValue) && oldValue.equals(currentValue)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 解锁
     *
     * @param key
     * @param value
     */
    public void unlock(String key, String value) {
        try {
            String currentValue = redisTemplate.opsForValue().get(key);
            if (StringUtils.hasLength(currentValue) && currentValue.equals(value)) {
                redisTemplate.opsForValue().getOperations().delete(key);
            }
        } catch (Exception e) {
            log.error("【redis分布式锁】解锁异常, {}", e);
        }
    }

}
