package com.atstar.sell.controller;

import com.atstar.sell.service.SecKillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by Dawn
 * 2017-08-06 23:16
 */
@RestController
@RequestMapping("/skill")
@Slf4j
public class SecKillController {

    @Resource
    private SecKillService secKillService;

    /**
     * 查询秒杀活动特价商品的信息
     *
     * @param productId
     * @return
     */
    @GetMapping("/query/{productId}")
    public String query(@PathVariable String productId) {
        return secKillService.querySecKillProductInfo(productId);
    }


    /**
     * 秒杀，没有抢到获得"哎呦喂,xxxxx",抢到了会返回剩余的库存量
     *
     * @param productId
     * @return
     */
    @GetMapping("/order/{productId}")
    public String skill(@PathVariable String productId) {
        log.info("@skill request, productId:" + productId);
        secKillService.orderProductMockDiffUser(productId);
        return secKillService.querySecKillProductInfo(productId);
    }
}
