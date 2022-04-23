package com.atstar.sell.config;

import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @Author: Dawn
 * @Date: 2022/4/21 23:46
 */
@Configuration
public class WechatOpenConfig {

    @Resource
    private WechatAccountConfig wechatAccountConfig;

    @Bean
    public WxMpService wxOpenService() {

        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxOpenConfigStorage());

        return wxMpService;
    }

    @Bean
    public WxMpConfigStorage wxOpenConfigStorage() {

        WxMpInMemoryConfigStorage wxMpConfigStorage = new WxMpInMemoryConfigStorage();

        wxMpConfigStorage.setAppId(wechatAccountConfig.getOpenAppId());
        wxMpConfigStorage.setSecret(wechatAccountConfig.getOpenAppSecret());

        return wxMpConfigStorage;
    }
}
