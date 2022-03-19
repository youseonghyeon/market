package com.project.market.modules.item.controller;

import com.project.market.WithAccount;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.dao.ItemRepository;
import com.project.market.modules.item.dao.ItemService;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.form.ItemForm;
import com.project.market.modules.security.AccountContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ItemService itemService;
    @Autowired
    ItemRepository itemRepository;

    @BeforeEach
    @WithAccount("testUser")
    void beforeEach() {
        AccountContext accountContext = (AccountContext)SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Account account = accountContext.getAccount();
        ItemForm itemForm = new ItemForm();
        itemForm.setName("test상품");
        itemForm.setPrice(8000);
        itemForm.setCoverPhoto("test.jpg");
        itemForm.setPhoto("test.jpg");
        itemForm.setOriginAddress("서울시 은평구");
        itemService.createNewItem(account, itemForm, new ArrayList<>());
    }

    @AfterEach
    @WithAccount("testUser")
    void afterEach() {
        itemRepository.deleteAll();
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("상품 등록 폼")
    void productEnrollForm() throws Exception {
        mockMvc.perform(get("/products/enroll"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("itemForm"))
                .andExpect(view().name("products/enroll"));
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("상품 등록")
    void productEnroll() throws Exception {
        mockMvc.perform(post("/products/enroll")
                        .param("name", "상품A")
                        .param("price", "6000")
                        .param("coverPhoto", "A.jpg")
                        .param("photo", "B.jpg")
                        .param("originAddress", "은평구 신사동")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Item item = itemRepository.findByName("상품A");
        assertEquals(item.getPrice(), 6000);
        assertEquals(item.getCoverPhoto(), "A.jpg");
        assertEquals(item.getPhoto(), "B.jpg");
        assertEquals(item.getOriginAddress(), "은평구 신사동");
        assertEquals(item.getShippingFee(), ItemService.DEFAULT_SHIPPING_FEE);
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("단일 상품 조회 폼")
    void productForm() throws Exception {
        Item item = itemRepository.findByName("test상품");
        mockMvc.perform(get("/deal/" + item.getId()))
                .andExpect(model().attributeExists("item"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product"));
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("전체 상품 리스트 조회 폼")
    void productList() throws Exception {
        mockMvc.perform(get("/products/list"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("itemList"))
                .andExpect(view().name("products/list"));
    }
}
