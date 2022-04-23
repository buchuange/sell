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
 * @Date: 2022/4/17 17:53
 */
@Configuration
public class WechatMpConfig {

    @Resource
    private WechatAccountConfig wechatAccountConfig;

    @Bean
    public WxMpService wxMpService() {

        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage());

        return wxMpService;
    }

    @Bean
    public WxMpConfigStorage wxMpConfigStorage() {

        WxMpInMemoryConfigStorage wxMpConfigStorage = new WxMpInMemoryConfigStorage();

        wxMpConfigStorage.setAppId(wechatAccountConfig.getMpAppId());
        wxMpConfigStorage.setSecret(wechatAccountConfig.getMpAppSecret());

        return wxMpConfigStorage;
    }

}
