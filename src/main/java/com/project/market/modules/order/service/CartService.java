package com.project.market.modules.order.service;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.order.entity.QCart;
import com.project.market.modules.order.repository.CartRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.project.market.modules.order.entity.QCart.cart;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final JPAQueryFactory queryFactory;

    public void deleteCartsByAccount(Account account) {
        queryFactory.delete(cart).where(cart.account.eq(account)).execute();
    }

}
