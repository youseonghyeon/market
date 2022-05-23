package com.project.market.modules.order.service;

import com.project.market.modules.order.entity.Cart;
import com.project.market.modules.order.repository.CartRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final JPAQueryFactory queryFactory;


    public void modifyQuantity(Cart cart, int newQuantity) {
        cart.modifyQuantity(newQuantity);
    }
}
