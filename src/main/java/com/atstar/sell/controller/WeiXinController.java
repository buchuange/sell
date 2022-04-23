package com.atstar.sell.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: Dawn
 * @Date: 2022/4/17 16:23
 */

/**
 * 手工获取oppenid
 */
@RestController
@RequestMapping("/weixin")
@Slf4j
public class WeiXinController {

    @GetMapping("/authorize")
    public void authorize(@RequestParam("code") String code) {

        log.info("进入authorize方法**********");

        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxba1faa1552caad36&secret=faac12722f965d9f4d7430ae004605c9&code=" + code + "&grant_type=authorization_code";

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        log.info("response={}", response);
    }
}
