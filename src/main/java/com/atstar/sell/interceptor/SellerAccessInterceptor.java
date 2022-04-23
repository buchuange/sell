package com.atstar.sell.interceptor;

import com.atstar.sell.consts.CookieConsts;
import com.atstar.sell.consts.RedisConsts;
import com.atstar.sell.exception.SellerAuthorizeException;
import com.atstar.sell.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: Dawn
 * @Date: 2022/4/22 14:21
 */
@Slf4j
public class SellerAccessInterceptor implements HandlerInterceptor {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 查询cookie
        Cookie cookie = CookieUtil.get(request, CookieConsts.TOKEN);

        if (ObjectUtils.isEmpty(cookie)) {
            log.warn("【登录校验】Cookie中查不到token");
            throw new SellerAuthorizeException();
        }

        // redis中查询
        String key = String.format(RedisConsts.TOKEN_PREFIX, cookie.getValue());
        String tokenValue = stringRedisTemplate.opsForValue().get(key);

        if (!StringUtils.hasLength(tokenValue)) {
            log.warn("【登录校验】Redis中不到token");
            throw new SellerAuthorizeException();
        }

        return true;
    }
}
