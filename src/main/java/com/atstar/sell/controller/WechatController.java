package com.atstar.sell.controller;

import com.atstar.sell.enums.ResultEnum;
import com.atstar.sell.exception.SellException;
import com.atstar.sell.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @Author: Dawn
 * @Date: 2022/4/17 16:23
 */

/**
 * 手工获取oppenid
 */
@Controller
@RequestMapping("/wechat")
@Slf4j
public class  WechatController {

    @Resource
    private WxMpService wxMpService;

    @Resource
    private WxMpService wxOpenService;

    @Value("${service.url}")
    private String serviceUrl;

    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl) throws UnsupportedEncodingException {

        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(serviceUrl + "/sell/wechat/userinfo", WxConsts.OAUTH2_SCOPE_USER_INFO, URLEncoder.encode(returnUrl, "utf-8"));

        log.info("【微信网页授权】获取code，result={}", redirectUrl);

        return "redirect:" + redirectUrl;
    }

    @GetMapping("/userinfo")
    public String userinfo(@RequestParam("code") String code,
                         @RequestParam("state") String state) {

        log.info("************获取用户信息**");

        WxMpOAuth2AccessToken wxMpOAuth2AccessToken;

        try {
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
        } catch (WxErrorException e) {
            log.error("【微信网页授权】{}", JsonUtil.print(e));
            throw new SellException(ResultEnum.WECHAT_MP_ERROR, e.getError().getErrorMsg());
        }

        String openid = wxMpOAuth2AccessToken.getOpenId();

        return "redirect:" + state + "?openid=" + openid;
    }

    @GetMapping("/qrAuthorize")
    public String qrAuthorize(@RequestParam("returnUrl") String returnUrl) throws UnsupportedEncodingException {

        String redirectUrl = wxMpService.buildQrConnectUrl(serviceUrl + "/sell/wechat/qrUserinfo", WxConsts.QRCONNECT_SCOPE_SNSAPI_LOGIN, URLEncoder.encode(returnUrl, "utf-8"));

        log.info("【卖家登录微信网页授权】获取code，result={}", redirectUrl);

        return "redirect:" + redirectUrl;
    }

    @GetMapping("/qrUserinfo")
    public String qrUserinfo(@RequestParam("code") String code,
                           @RequestParam("state") String state) {

        log.info("********卖家登录获取用户信息**");

        WxMpOAuth2AccessToken wxMpOAuth2AccessToken;

        try {
            wxMpOAuth2AccessToken = wxOpenService.oauth2getAccessToken(code);
        } catch (WxErrorException e) {
            log.error("【卖家登录微信网页授权】{}", JsonUtil.print(e));
            throw new SellException(ResultEnum.WECHAT_MP_ERROR, e.getError().getErrorMsg());
        }

        String openid = wxMpOAuth2AccessToken.getOpenId();

        return "redirect:" + state + "?openid=" + openid;
    }

}
