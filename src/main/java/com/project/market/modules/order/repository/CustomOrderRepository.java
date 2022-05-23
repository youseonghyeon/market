package com.project.market.modules.order.repository;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.order.entity.Order;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomOrderRepository {

    List<Order> findOrdersWithCartAndItemAndDeliveryByAccount(Account account, Pageable pageable);
}
