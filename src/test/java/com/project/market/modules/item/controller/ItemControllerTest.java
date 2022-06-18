package com.project.market.modules.item.controller;

import com.project.market.WithAccount;
import com.project.market.infra.MockItem;
import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.item.service.ItemService;
import com.project.market.modules.item.entity.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    MockItem mockItem;

    @BeforeEach
    @WithAccount("testUser")
    void beforeEach() {
        mockItem.createMockItem("test상품");
    }

    @AfterEach
    @WithAccount("testUser")
    void afterEach() {
        itemRepository.deleteAll();
    }

    @Test
    @WithAccount("testAdmin")
    @DisplayName("상품 등록 폼")
    void productEnrollForm() throws Exception {
        mockMvc.perform(get("/product/enroll"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("itemForm"))
                .andExpect(view().name("products/enroll"));
    }


    /**
     * multipartFormdata 전송 방법을 배우고 난 후 작성
     * @throws Exception
     */
//    @Test
//    @WithAccount("testAdmin")
//    @DisplayName("상품 등록")
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
        assertEquals(item.getCoverPhotoPath(), "A.jpg");
        assertEquals(item.getPhotoPath(), "B.jpg");
    }

    @Test
    @WithAccount("testAdmin")
    @DisplayName("내 상품 수정 폼")
    void editMyProductForm() throws Exception {
        Item item = itemRepository.findByName("test상품");
        mockMvc.perform(get("/product/modify/" + item.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("itemForm"))
                .andExpect(view().name("products/edit"));
    }

//    @Test
//    @WithAccount("testAdmin")
//    @DisplayName("상품 수정")
    void modifyProduct() throws Exception {
        Item item = mockItem.createMockItem("상품");
        MockMultipartFile file = new MockMultipartFile("이미지", "imagefile.jpeg", "image/jpeg", "<<jpeg data>>".getBytes());

        mockMvc.perform(put("/product/modify/" + item.getId().toString())
                        .param("name", "수정된 상품")
                        .param("price", "4000")
                        .param("quantity", "4000")
                        .param("description", "상세 설명1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product/" + item.getId()));

        Item modifiedItem = itemRepository.findByName("수정된 상품");
        assertNotNull(modifiedItem);
        assertEquals(modifiedItem.getPrice(), 4000);
        assertEquals(modifiedItem.getCoverPhotoPath(), "없음");
        assertEquals(modifiedItem.getPhotoPath(), "없음");
        assertEquals(modifiedItem.getDescription(), "상세 설명1");

    }

}
