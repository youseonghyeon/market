package com.project.market.modules.order.dao;

import com.project.market.modules.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface OrderRepository extends JpaRepository<Orders, Long> {
}
