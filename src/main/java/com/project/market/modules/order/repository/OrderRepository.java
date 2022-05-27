package com.project.market.modules.order.repository;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.order.entity.OrderStatus;
import com.project.market.modules.order.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface OrderRepository extends JpaRepository<Order, Long>, CustomOrderRepository {

    @EntityGraph(value = "withItemAndDelivery", type = EntityGraph.EntityGraphType.FETCH)
    List<Order> findByCustomerAndOrderStatusIsOrderByCreatedDateDesc(Account account, OrderStatus orderStatus);

    @EntityGraph(value = "withItemAndDelivery", type = EntityGraph.EntityGraphType.FETCH)
    List<Order> findByCustomerOrderByCreatedDateDesc(Account account);
    @EntityGraph(value = "withItemAndDelivery", type = EntityGraph.EntityGraphType.FETCH)
    List<Order> findByCustomerIdOrderByCreatedDateDesc(Long customerId);

    @EntityGraph(attributePaths = {"customer"}, type = EntityGraph.EntityGraphType.FETCH)
    Order findOrderWithDeliveryById(Long orderId);

    List<Order> findOrdersByOrderStatusAndPaymentMethodOrderByCreatedDateAsc(OrderStatus orderStatus, String method);

    @EntityGraph(attributePaths = {"carts"}, type = EntityGraph.EntityGraphType.FETCH)
    Order findWithCartsById(Long orderId);
}
