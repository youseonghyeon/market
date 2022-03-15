package com.project.market.modules.order.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.order.entity.OrderStatus;
import com.project.market.modules.order.entity.Orders;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface OrderRepository extends JpaRepository<Orders, Long> {

//    @EntityGraph(value = "Study.withItem", type = EntityGraph.EntityGraphType.LOAD)
    List<Orders> findByCustomerAndOrderStatusIs(Account account, OrderStatus orderStatus);

//    @EntityGraph(value = "Study.withItem", type = EntityGraph.EntityGraphType.LOAD)
    List<Orders> findByCustomer(Account account);
}
