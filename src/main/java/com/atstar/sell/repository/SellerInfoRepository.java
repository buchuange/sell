package com.atstar.sell.repository;

import com.atstar.sell.domain.SellerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerInfoRepository extends JpaRepository<SellerInfo, Long> {

    SellerInfo findByOpenid(String openid);
}
