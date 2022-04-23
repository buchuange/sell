package com.atstar.sell.service;

import com.atstar.sell.domain.SellerInfo;

public interface SellerService {

    SellerInfo findByOpenid(String openid);
}
