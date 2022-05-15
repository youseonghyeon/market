package com.project.market.modules.item.controller;

import com.project.market.WithAccount;
import com.project.market.infra.MockDelivery;
import com.project.market.infra.MockItem;
import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.dao.repository.ItemRepository;
import com.project.market.modules.item.dao.ItemService;
import com.project.market.modules.item.entity.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
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
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    MockDelivery mockDelivery;
    @Autowired
    MockItem mockItem;

    @BeforeEach
    @WithAccount("testUser")
    void beforeEach() {
        Account account = accountRepository.findByLoginId("testUser");
        mockItem.createMockItem(account, "test상품");
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
        mockMvc.perform(get("/product/enroll"))
                .andExpect(model().attributeExists("itemForm"))
                .andExpect(view().name("products/enroll"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("상품 등록")
    void productEnroll() throws Exception {
        mockMvc.perform(post("/product/enroll")
                        .param("name", "상품A")
                        .param("price", "6000")
//                        .param("coverPhoto", "A.jpg")
//                        .param("photo", "B.jpg")
                        .param("originAddress", "은평구 신사동")
                        .param("description", "설명")
                        .param("post", "true")
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
    @DisplayName("내 상품 수정 폼")
    void editMyProductForm() throws Exception {
        Item item = itemRepository.findByName("test상품");
        mockMvc.perform(get("/product/edit/" + item.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("itemForm"))
                .andExpect(view().name("products/edit"));
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("상품 수정")
    void modifyProduct() throws Exception {
        Item item = itemRepository.findByName("test상품");

        mockMvc.perform(post("/product/modify")
                        .param("id", item.getId().toString())
                        .param("name", "수정된 상품")
                        .param("price", "4000")
                        .param("coverPhoto", "없음")
                        .param("photo", "없음")
                        .param("originAddress", "잠실")
                        .param("description", "상세 설명1")
                        .param("direct", "true")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product/" + item.getId()));

        Item modifiedItem = itemRepository.findByName("수정된 상품");
        assertNotNull(modifiedItem);
        assertEquals(modifiedItem.getPrice(), 4000);
        assertEquals(modifiedItem.getCoverPhoto(), "없음");
        assertEquals(modifiedItem.getPhoto(), "없음");
        assertEquals(modifiedItem.getOriginAddress(), "잠실");
        assertEquals(modifiedItem.getDescription(), "상세 설명1");
        assertFalse(modifiedItem.isPost());
        assertTrue(modifiedItem.isDirect());

    }

}
