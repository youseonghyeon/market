package com.project.market.modules.order.dao;

import com.project.market.infra.TestUtils;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.entity.Order;
import com.project.market.modules.order.entity.OrderStatus;
import com.project.market.modules.order.form.OrderForm;
import com.project.market.modules.order.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
class OrderServiceTest {

    @Autowired
    TestUtils testUtils;
    @Autowired
    OrderService orderService;
    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("")
    void createOrder() {
        //given
        Account seller = testUtils.createMockAccount("seller");
        Account buyer = testUtils.createMockAccount("buyer");
        Item item = testUtils.createMockItem(seller, "item00");
        OrderForm orderForm = new OrderForm( buyer);
        //when
        Order order = orderService.createOrder(buyer, null, null);
        //then
        assertEquals(order.getOrderStatus(), OrderStatus.WAITING);
        /**
         * TODO 카드 결제 시 카드로 처리
         */
    }

    @Test
    void findOrders() {
    }

    @Test
    void testFindOrders() {
    }

    @Test
    void join() {
    }

    @Test
    void payment() {
    }
}
