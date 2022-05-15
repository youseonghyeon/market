package com.project.market.modules.order.dao;

import com.project.market.infra.MockAccount;
import com.project.market.infra.MockDelivery;
import com.project.market.infra.MockItem;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.dao.repository.ItemRepository;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.entity.Order;
import com.project.market.modules.order.entity.OrderStatus;
import com.project.market.modules.order.form.OrderForm;
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
    MockDelivery testUtils;
    @Autowired
    OrderService orderService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    MockAccount mockAccount;

    @Autowired
    MockItem mockItem;

    @Test
    @DisplayName("")
    void createOrder() {
        //given
        Account seller = mockAccount.createMockAccount("seller");
        Account buyer = mockAccount.createMockAccount("buyer");
        Item item = mockItem.createMockItem(seller, "item00");
        OrderForm orderForm = new OrderForm(item.getId(), "post", buyer);
        //when
        Order order = orderService.createOrder(buyer, orderForm, item);
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
