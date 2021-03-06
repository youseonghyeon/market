package com.project.market.modules.order.controller;

import com.project.market.WithAccount;
import com.project.market.infra.MockAccount;
import com.project.market.infra.MockDelivery;
import com.project.market.infra.MockItem;
import com.project.market.infra.MockOrder;
import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.item.service.ItemService;
import com.project.market.modules.item.repository.TagRepository;
import com.project.market.modules.item.service.TagService;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.repository.OrderRepository;
import com.project.market.modules.order.entity.Order;
import com.project.market.modules.order.entity.OrderStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired TagRepository tagRepository;
    @Autowired TagService tagService;
    @Autowired ItemService itemService;
    @Autowired ItemRepository itemRepository;
    @Autowired OrderRepository orderRepository;
    @Autowired
    MockDelivery mockDelivery;
    @Autowired
    MockAccount mockAccount;
    @Autowired
    MockItem mockItem;

    @Autowired
    MockOrder mockOrder;


    @BeforeEach
    void beforeEach() {
        Account account = mockAccount.createMockAccount("mockAccount");
        Item item = mockItem.createMockItem(account, "mockItem");
    }

    @AfterEach
    void afterEach() {
        orderRepository.deleteAll();
        itemRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("?????? ?????? ???")
    void purchaseForm() throws Exception {
        Item item = itemRepository.findByName("mockItem");
        mockMvc.perform(get("/purchase")
                        .param("itemId", item.getId().toString())
                        .param("method", item.isPost() ? "post" : "direct"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("orderForm"))
                .andExpect(model().attributeExists("item"))
                .andExpect(view().name("order/purchase"));
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("?????? ??????")
    void purchase() throws Exception {
        Item item = itemRepository.findByName("mockItem");
        mockMvc.perform(post("/purchase")
                        .param("itemId", item.getId().toString())
                        .param("shippingRequests", "?????? ????????????")
                        .param("recipient", "????????? ??????")
                        .param("recipientPhone", "01011002200")
                        .param("destinationZoneCode", "12334")
                        .param("destinationAddress", "?????? ??????")
                        .param("destinationAddressDetail", "?????? ??????")
                        .param("paymentMethod", "card")
                        .param("deliveryMethod", item.isPost() ? "post" : "direct")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Item findItem = itemRepository.findByName("mockItem");
        Account customer = accountRepository.findByLoginId("testUser");

        List<Order> orders = orderRepository.findByCustomerOrderByOrderDateDesc(customer);
        Order order = orders.get(0);
        assertEquals(order.getOrderStatus(), OrderStatus.WAITING);
        assertEquals(order.getOrderedItem(), findItem);
        assertEquals(order.getCustomer(), customer);
        assertEquals(order.getPaymentMethod(), "card");
        assertEquals(order.getShippingRequests(), "?????? ????????????");
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("?????? ?????? ???")
    void orderDetailForm() throws Exception {
        Account account = accountRepository.findByLoginId("testUser");
        Item item = itemRepository.findByName("mockItem");
        Order order = mockOrder.createMockOrder(account, item.getId());
        mockMvc.perform(get("/order/" + order.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("order"))
                .andExpect(view().name("order/detail"));
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("?????? ????????? ???")
    void orderListForm() throws Exception {
        mockMvc.perform(get("/order/list"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("orderList"))
                .andExpect(view().name("order/list"));
    }

}

