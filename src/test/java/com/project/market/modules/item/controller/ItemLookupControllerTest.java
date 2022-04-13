package com.project.market.modules.item.controller;

import com.project.market.WithAccount;
import com.project.market.infra.TestUtils;
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
    TestUtils testUtils;

    @BeforeEach
    @WithAccount("testUser")
    void beforeEach() {
        Account account = accountRepository.findByLoginId("testUser");
        testUtils.createMockItem(account, "test상품");
    }

    @AfterEach
    @WithAccount("testUser")
    void afterEach() {
        itemRepository.deleteAll();
    }


    @Test
    @WithAccount("testUser")
    @DisplayName("단일 상품 조회 폼")
    void productForm() throws Exception {
        Item item = itemRepository.findByName("test상품");
        mockMvc.perform(get("/product/" + item.getId()))
                .andExpect(model().attributeExists("item"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product"));
    }

    @Test
    @DisplayName("전체 상품 리스트 조회 폼(권한X)")
    void productList() throws Exception {
        mockMvc.perform(get("/product/list"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("itemList"))
                .andExpect(model().attributeExists("itemPage"))
                .andExpect(model().attributeExists("tagList"))
                .andExpect(view().name("products/list"));
    }


    @Test
    @WithAccount("testUser")
    @DisplayName("내 상품 리스트 폼")
    void myProductListForm() throws Exception {
        mockMvc.perform(get("/product/my-list"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("itemList"))
                .andExpect(view().name("products/my-list"));
    }
}
