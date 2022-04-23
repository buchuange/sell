package com.atstar.sell.repository;

import com.atstar.sell.domain.OrderMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderMasterRepository extends JpaRepository<OrderMaster, Long> {

    Page<OrderMaster> findByBuyerOpenid(String buyerOpenId, Pageable pageable);
}
