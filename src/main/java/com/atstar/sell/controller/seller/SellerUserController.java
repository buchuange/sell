package com.atstar.sell.controller.seller;

import cn.hutool.core.util.IdUtil;
import com.atstar.sell.consts.CookieConsts;
import com.atstar.sell.consts.RedisConsts;
import com.atstar.sell.domain.SellerInfo;
import com.atstar.sell.enums.ResultEnum;
import com.atstar.sell.service.SellerService;
import com.atstar.sell.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * 卖家用户
 *
 * @Author: Dawn
 * @Date: 2022/4/22 00:15
 */
@Slf4j
@Controller
@RequestMapping("/seller")
public class SellerUserController {

    @Resource
    private SellerService sellerService;

    @Resource    // @Resource是默认取字段名进行按照名称注入 不能写redisTemplate
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/login")
    public String login(@RequestParam("openid") String openid, Model model, HttpServletResponse response) {

        // 1、openid和数据库中数据进行匹配
        SellerInfo sellerInfo = sellerService.findByOpenid(openid);
        if (ObjectUtils.isEmpty(sellerInfo)) {

            model.addAttribute("msg", ResultEnum.LOGIN_FAIL.getMsg());
            model.addAttribute("url", "/seller/order/list");

            return "common/error";
        }

        // 2、设置token至redis
        String token = IdUtil.simpleUUID();
        int expire = RedisConsts.EXPIRE;

        stringRedisTemplate.opsForValue().set(String.format(RedisConsts.TOKEN_PREFIX, token), openid, expire, TimeUnit.SECONDS);

        // 2、设置token至cookie
        CookieUtil.set(response, CookieConsts.TOKEN, token, expire);

        return "redirect:/seller/order/list";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Model model) {

        // 1、从cookie里查询
        Cookie cookie = CookieUtil.get(request, CookieConsts.TOKEN);

        if (ObjectUtils.isEmpty(cookie)) {

            model.addAttribute("msg", ResultEnum.LOGOUT_FAIL.getMsg());
            model.addAttribute("url", "/seller/order/list");

            return "common/error";
        }

        // 2、清除redis
        stringRedisTemplate.opsForValue().getOperations().delete(String.format(RedisConsts.TOKEN_PREFIX, cookie.getValue()));

        // 3、清除cookie
        CookieUtil.set(response, CookieConsts.TOKEN, null, 0);

        model.addAttribute("msg", ResultEnum.LOGOUT_SUCCESS.getMsg());
        model.addAttribute("url", "/seller/order/list");

        return "common/success";
    }
}
