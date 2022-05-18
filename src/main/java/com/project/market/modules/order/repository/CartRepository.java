package com.project.market.modules.order.repository;

import com.project.market.modules.order.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
