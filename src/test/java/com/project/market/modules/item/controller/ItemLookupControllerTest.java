package com.project.market.modules.item.controller;

import com.project.market.WithAccount;
import com.project.market.infra.MockItem;
import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.item.service.ItemService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class ItemLookupControllerTest {

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
    MockItem mockItem;

    @BeforeEach
    void beforeEach() {
        mockItem.createMockItem("test상품");
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
    }


    @Test
    @DisplayName("단일 상품 조회 폼 (비회원)")
    void productForm() throws Exception {
        Item item = itemRepository.findByName("test상품");

        mockMvc.perform(get("/product/" + item.getId()))
                .andExpect(model().attribute("item", item))
                .andExpect(model().attribute("favorite", false))
                .andExpect(view().name("products/product"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("삭제된 상품 조회 (Ex)")
    void productForm_deletedItem() throws Exception {
        Item item = itemRepository.findByName("test상품");
        item.delete();

        mockMvc.perform(get("/product/" + item.getId()))
                .andExpect(view().name("404"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("전체 상품 리스트 조회 폼(비회원)")
    void productList() throws Exception {
        mockMvc.perform(get("/product/list"))
                .andExpect(model().attributeExists("itemList"))
                .andExpect(model().attributeExists("itemPage"))
                .andExpect(model().attributeExists("tagList"))
                .andExpect(view().name("products/list"))
                .andExpect(status().isOk());
    }


    // 어드민 페이지로 이동
//    @Test
//    @WithAccount("testUser")
//    @DisplayName("내 상품 리스트 폼")
//    void myProductListForm() throws Exception {
//        mockMvc.perform(get("/product/my-list"))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("itemList"))
//                .andExpect(view().name("products/my-list"));
//    }

    @Test
    @WithAccount("testUser")
    @DisplayName("관심 상품 리스트 폼")
    void favoriteListForm() throws Exception {
        mockMvc.perform(get("/favorite/list"))
                .andExpect(model().attributeExists("favoriteList"))
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name("products/favorite-list"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("장바구니 목록 폼")
    void myCart() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(view().name("products/cart"))
                .andExpect(status().isOk());
    }
}
