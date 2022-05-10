package com.project.market.modules.delivery.dao;

import com.project.market.modules.order.entity.Order;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.market.modules.delivery.entity.QDelivery.delivery;
import static com.project.market.modules.order.entity.QOrder.order;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryRepositoryImpl implements CustomDeliveryRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public boolean isMyDelivery(Long accountId, Long deliveryId) {
        List<Order> orders = queryFactory.select(order)
                .from(order)
                .join(order.orderDelivery, delivery)
                .where(order.customer.id.eq(accountId),
                        delivery.id.eq(deliveryId)).fetch();
        return orders.size() == 1;
    }
}
