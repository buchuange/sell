package com.atstar.sell.config;

import com.lly835.bestpay.config.AliPayConfig;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @Author: Dawn
 * @Date: 2022/4/18 19:33
 */
@Configuration
public class WechatPayConfig {

    @Resource
    private WechatAccountConfig wechatAccountConfig;

    @Bean
    public BestPayService bestPayService() {

        BestPayServiceImpl bestPayService = new BestPayServiceImpl();
        bestPayService.setWxPayConfig(wxPayConfig());

        return bestPayService;
    }

    @Bean
    public WxPayConfig wxPayConfig() {

        WxPayConfig wxPayConfig = new WxPayConfig();
        wxPayConfig.setAppId(wechatAccountConfig.getMpAppId());
        wxPayConfig.setAppSecret(wechatAccountConfig.getMpAppSecret());
        wxPayConfig.setMchId(wechatAccountConfig.getMchId());
        wxPayConfig.setMchKey(wechatAccountConfig.getMchKey());
        wxPayConfig.setKeyPath(wechatAccountConfig.getKeyPath());
        wxPayConfig.setNotifyUrl(wechatAccountConfig.getNotifyUrl());

        return wxPayConfig;
    }
}
