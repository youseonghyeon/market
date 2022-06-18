package com.project.market.modules.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.market.WithAccount;
import com.project.market.infra.MockCart;
import com.project.market.infra.MockItem;
import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.item.service.ItemService;
import com.project.market.modules.item.repository.TagRepository;
import com.project.market.modules.item.service.TagService;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.dto.PurchaseRes;
import com.project.market.modules.order.entity.Cart;
import com.project.market.modules.order.repository.OrderRepository;
import com.project.market.modules.order.entity.Order;
import com.project.market.modules.order.entity.OrderStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    TagService tagService;
    @Autowired
    ItemService itemService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    MockCart mockCart;
    @Autowired
    MockItem mockItem;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ObjectMapper objectMapper;

    @Value("${shipping.fee}")
    int shippingFee;

    @BeforeEach
    void beforeEach() {

    }

    @AfterEach
    void afterEach() {
        orderRepository.deleteAll();
        itemRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("상품 구매 폼(cartId를 이용한 상품 구매 폼)")
    void purchaseFormWithCartId() throws Exception {
        //given
        Item item1 = mockItem.createMockItem("item1");
        Item item2 = mockItem.createMockItem("item2");

        Account account = accountRepository.findByLoginId("testUser");
        Cart savedCart1 = mockCart.createMockCart(account, item1, 10);
        Cart savedCart2 = mockCart.createMockCart(account, item2, 5);

        String param = savedCart1.getId() + "," + savedCart2.getId();

        //when
        mockMvc.perform(get("/purchase")
                        .param("items", param))
                .andExpect(view().name("order/purchase"))
                .andExpect(model().attributeExists("orderForm", "cartItems",
                        "deliveryFee", "totalPrice", "account", "items"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("상품 구매 폼(상품:수량 을 이용한 상품 구매 폼)")
    void purchaseFormWithItemIdAndQuantity() throws Exception {
        Item item1 = mockItem.createMockItem("item1");
        Item item2 = mockItem.createMockItem("item2");
        String param = item1.getId() + ":9," + item2.getId() + ":11,";

        mockMvc.perform(get("/purchase")
                        .param("items", param))
                .andExpect(view().name("order/purchase"))
                .andExpect(model().attributeExists("orderForm", "cartItems",
                        "deliveryFee", "totalPrice", "account", "items"))
                .andExpect(status().isOk());

    }


    @Test
    @WithAccount("testUser")
    @DisplayName("상품 구매")
    void purchase() throws Exception {
        //given
        Item item1 = mockItem.createMockItem("item1");
        Item item2 = mockItem.createMockItem("item2");

        Account account = accountRepository.findByLoginId("testUser");
        Cart savedCart1 = mockCart.createMockCart(account, item1, 10);
        Cart savedCart2 = mockCart.createMockCart(account, item2, 5);
        // TODO Cart 와 Item의 상품 가격이 동일한지 확인 (메서드가 복잡해서 밖으로 빼야함)
        int expectedTotalPrice = item1.getPrice() * 10 + item2.getPrice() * 5 + shippingFee;
        assertEquals(savedCart1.getPrice() + savedCart2.getPrice(),
                item1.getPrice() * 10 + item2.getPrice() * 5);

        String param = savedCart1.getId() + "," + savedCart2.getId();
        String shippingRequest = "배송 요청사항";
        String buyerName = "홍길동";
        String buyerPhone = "01099888877";
        String zoneCode = "12345";
        String address = "은평터널로 100";
        String addressDetail = "100동 100호";
        String paymentMethod = "card";

        MvcResult result = mockMvc.perform(post("/purchase")
                        .param("items", param)
                        .param("shippingRequests", shippingRequest)
                        .param("recipient", buyerName)
                        .param("recipientPhone", buyerPhone)
                        .param("destinationZoneCode", zoneCode)
                        .param("destinationAddress", address)
                        .param("destinationAddressDetail", addressDetail)
                        .param("paymentMethod", paymentMethod)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        PurchaseRes purchaseRes = objectMapper.readValue(content, PurchaseRes.class);
        assertNotNull(purchaseRes);

        Long orderId = purchaseRes.getOrderId();

        Order order = orderRepository.findById(orderId).orElseThrow();
        assertEquals(order.getOrderStatus(), OrderStatus.WAITING);
        assertEquals(order.getPaymentMethod(), paymentMethod);
        assertEquals(order.getBuyerName(), buyerName);
        assertEquals(order.getBuyerPhone(), buyerPhone);
        assertEquals(order.getShippingRequests(), shippingRequest);
        assertEquals(order.getDestinationZoneCode(), zoneCode);
        assertEquals(order.getDestinationAddress(), address);
        assertEquals(order.getDestinationAddressDetail(), addressDetail);
        assertEquals(order.getTotalPrice(), expectedTotalPrice);
        assertEquals(order.getShippingFee(), shippingFee);
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("주문 상세 폼")
    void orderDetailForm() throws Exception {
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("주문 리스트 폼")
    void orderListForm() throws Exception {
        mockMvc.perform(get("/order/list"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("orderList"))
                .andExpect(view().name("order/history"));
    }

}

