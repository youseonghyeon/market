package com.project.market.modules.order.repository;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.order.entity.Order;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.market.modules.item.entity.QItem.item;
import static com.project.market.modules.order.entity.QCart.cart;
import static com.project.market.modules.order.entity.QOrder.order;

@Repository
@Transactional
@RequiredArgsConstructor
public class OrderRepositoryImpl implements CustomOrderRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Order> findOrdersWithCartAndItemAndDeliveryByAccount(Account account, Pageable pageable) {
        return queryFactory.select(order).distinct()
                .from(order)
                .join(order.carts, cart).fetchJoin()
                .join(cart.item, item).fetchJoin()
                .orderBy(order.orderDate.desc()).fetch();
    }
}
