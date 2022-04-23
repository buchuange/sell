package com.atstar.sell.service.impl;

import com.atstar.sell.domain.SellerInfo;
import com.atstar.sell.repository.SellerInfoRepository;
import com.atstar.sell.service.SellerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: Dawn
 * @Date: 2022/4/21 23:13
 */
@Service
public class SellerServiceImpl implements SellerService {

    @Resource
    private SellerInfoRepository repository;

    @Override
    public SellerInfo findByOpenid(String openid) {
        return repository.findByOpenid(openid);
    }
}
