package com.project.market.modules.order.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.order.entity.OrderStatus;
import com.project.market.modules.order.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(value = "withItemAndDelivery", type = EntityGraph.EntityGraphType.FETCH)
    List<Order> findByCustomerAndOrderStatusIsOrderByOrderDateTimeDesc(Account account, OrderStatus orderStatus);

    @EntityGraph(value = "withItemAndDelivery", type = EntityGraph.EntityGraphType.FETCH)
    List<Order> findByCustomerOrderByOrderDateTimeDesc(Account account);

}
