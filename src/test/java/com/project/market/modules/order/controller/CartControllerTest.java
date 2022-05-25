package com.project.market.modules.order.controller;

import com.project.market.WithAccount;
import com.project.market.infra.MockCart;
import com.project.market.infra.MockItem;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.order.entity.Cart;
import com.project.market.modules.order.repository.CartRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class CartControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    MockItem mockItem;
    @Autowired
    MockCart mockCart;
    @Autowired
    CartRepository cartRepository;

    List<Cart> savedCartList = new ArrayList<>();

    @BeforeEach
    @WithAccount("testUser")
    void init() {
        Account testUser = accountRepository.findByLoginId("testUser");
        Item item1 = mockItem.createMockItem("item1");
        Item item2 = mockItem.createMockItem("item2");
        mockItem.createMockItem("item3");
        mockItem.createMockItem("item4");
        Cart mockCart1 = mockCart.createMockCart(testUser, item1, 3);
        Cart mockCart2 = mockCart.createMockCart(testUser, item2, 5);
        savedCartList.add(mockCart1);
        savedCartList.add(mockCart2);

    }

    @AfterEach
    void reset() {
        cartRepository.deleteAll();
        itemRepository.deleteAll();
    }


    @Test
    @WithAccount("testUser")
    @DisplayName("장바구니 폼")
    void cartForm() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(model().attributeExists("cartList"))
                .andExpect(model().attributeExists("shippingFee"))
                .andExpect(model().attributeExists("totalPrice"))
                .andExpect(view().name("products/cart"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("Cart 추가")
    void addCart() throws Exception {
        Item item = mockItem.createMockItem("item0099");
        mockMvc.perform(get("/cart/add")
                        .param("itemId", String.valueOf(item.getId()))
                        .param("quantity", "3"))
                .andExpect(content().string("장바구니에 저장되었습니다."))
                .andExpect(status().isOk());

        List<Cart> cartList = cartRepository.findAll();
        cartList.stream().anyMatch(cart -> {
            System.out.println("cart = " + cart.getItem().getName());
            return cart.getItem().getName().equals("item0099");
        });
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("Cart 추가Ex(수량 부족)")
    void addCartQuantityEx() throws Exception {
//        Item item = mockItem.createMockItem("item0099");
//        mockMvc.perform(get("/cart/add")
//                        .param("itemId", String.valueOf(item.getId()))
//                        .param("quantity", "9999"))
//                .andExpect(content().string("장바구니에 저장되었습니다."))
//                .andExpect(status().isOk());
//
//        List<Cart> cartList = cartRepository.findAll();
//        cartList.stream().anyMatch(cart -> {
//            System.out.println("cart = " + cart.getItem().getName());
//            return cart.getItem().getName().equals("item0099");
//        });
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("Cart 추가Ex(이미 저장한 상품)")
    void addCartPreviousSavedEx() throws Exception {
//        Item item = mockItem.createMockItem("item0099");
//        mockMvc.perform(get("/cart/add")
//                        .param("itemId", String.valueOf(item.getId()))
//                        .param("quantity", "3"))
//                .andExpect(content().string("장바구니에 저장되었습니다."))
//                .andExpect(status().isOk());
//
//        List<Cart> cartList = cartRepository.findAll();
//        cartList.stream().anyMatch(cart -> {
//            System.out.println("cart = " + cart.getItem().getName());
//            return cart.getItem().getName().equals("item0099");
//        });
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("Cart 삭제")
    void deleteCart() throws Exception {
        Long savedCartId = savedCartList.get(0).getId();

        mockMvc.perform(get("/cart/delete")
                        .param("cartId", String.valueOf(savedCartId)))
                .andExpect(content().string("ok"))
                .andExpect(status().isOk());

        Optional<Cart> findCart = cartRepository.findById(savedCartId);
        assertTrue(findCart.isEmpty());
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("Cart 전체 삭제")
    void deleteAllOfCart() throws Exception {
        Account account = accountRepository.findByLoginId("testUser");
        mockMvc.perform(get("/cart/delete/all"))
                .andExpect(content().string("ok"))
                .andExpect(status().isOk());

        Set<Cart> cartList = cartRepository.findCartsByAccount(account);
        assertTrue(cartList.isEmpty());
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("Cart 수량 변경")
    void modifyQuantityInCart() throws Exception {
        Cart cart = savedCartList.get(0);
        Long cartId = cart.getId();
        int previousQuantity = cart.getQuantity();
        int expectedTotalPrice = cart.getItem().getPrice() * (previousQuantity + 5);

        mockMvc.perform(post("/quantity/modify")
                        .param("cartId", String.valueOf(cartId))
                        .param("quantity", String.valueOf(previousQuantity + 5))
                        .with(csrf()))
                .andExpect(content().string(String.valueOf(expectedTotalPrice)))
                .andExpect(status().isOk());

        Cart findCart = cartRepository.findById(cartId).orElseThrow();
        assertEquals(findCart.getQuantity(), previousQuantity + 5);
    }
}
