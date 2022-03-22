package com.project.market.modules.account.controller;

import com.project.market.WithAccount;
import com.project.market.modules.account.dao.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
class AdminControllerTest {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    MockMvc mockMvc;

    String URL_PREFIX = "/admin";

    @Test
    @WithAccount("testAdmin")
    @DisplayName("회원관리 폼")
    void manageForm() throws Exception {
        mockMvc.perform(get(URL_PREFIX + "/manage"))
                .andExpect(model().attributeExists("accountList"))
                .andExpect(view().name("admin/management"));
    }

    @Test
    @WithAccount("testAdmin")
    @DisplayName("배송사 관리")
    void currierManagement() throws Exception {

    }

    @Test
    @WithAccount("testAdmin")
    @DisplayName("배송사 관리2")
    void deliveryManagement() throws Exception {
        mockMvc.perform(get(URL_PREFIX + "/delivery/manage"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/delivery-manage"));

    }

}
