package com.project.market.modules.order.repository;

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
    List<Order> findByCustomerAndOrderStatusIsOrderByOrderDateDesc(Account account, OrderStatus orderStatus);

    @EntityGraph(value = "withItemAndDelivery", type = EntityGraph.EntityGraphType.FETCH)
    List<Order> findByCustomerOrderByOrderDateDesc(Account account);
    @EntityGraph(value = "withItemAndDelivery", type = EntityGraph.EntityGraphType.FETCH)
    List<Order> findByCustomerIdOrderByOrderDateDesc(Long customerId);

    @EntityGraph(attributePaths = {"customer"}, type = EntityGraph.EntityGraphType.FETCH)
    Order findOrderWithDeliveryById(Long orderId);

    List<Order> findOrdersByOrderStatusAndPaymentMethodOrderByOrderDateAsc(OrderStatus orderStatus, String method);
}
