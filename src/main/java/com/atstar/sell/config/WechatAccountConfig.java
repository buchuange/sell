package com.atstar.sell.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: Dawn
 * @Date: 2022/4/17 17:59
 */
@Component
@Data
@ConfigurationProperties(prefix = "wechat")
public class WechatAccountConfig {

    /**
     * 公众平台
     */
    private String mpAppId;

    private String mpAppSecret;

    /**
     * 开放平台
     */
    private String openAppId;

    private String openAppSecret;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户密钥
     */
    private String mchKey;

    /**
     * 商户证书路径
     */
    private String keyPath;

    /**
     * 支付完成后的异步通知地址.
     */
    private String notifyUrl;

    /**
     * 模板消息
     */
    private Map<String, String> templateId;
}
