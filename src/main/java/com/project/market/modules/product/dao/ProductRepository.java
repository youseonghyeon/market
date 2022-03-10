package com.project.market.modules.product.dao;

import com.project.market.modules.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ProductRepository extends JpaRepository<Product, Long> {
}
