package com.project.market.modules.account.controller;

import com.project.market.WithAccount;
import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.order.entity.Order;
import com.project.market.modules.order.entity.OrderStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    MockMvc mockMvc;


    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @WithAccount("testAdmin")
    @DisplayName("회원관리 폼")
    void manageForm() throws Exception {
        mockMvc.perform(get("/admin/manage"))
                .andExpect(model().attributeExists("accountList"))
                .andExpect(view().name("admin/management"));
    }

    @Test
    @WithAccount("testAdmin")
    @DisplayName("권한 변경 폼")
    void editRoleForm() throws Exception {
        Account account = accountRepository.findByLoginId("testAdmin");
        mockMvc.perform(get("/admin/manage/" + account.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name("admin/edit-management"));
    }

    @Test
    @WithAccount("testAdmin")
    @DisplayName("권한 변경")
    void editRole() throws Exception {
        Account account = accountRepository.findByLoginId("testAdmin");
        mockMvc.perform(post("/admin/manage/edit")
                        .param("targetId", account.getId().toString())
                        .param("role", "ROLE_USER")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/manage/" + account.getId()));
        Account target = accountRepository.findByLoginId("testAdmin");
        assertEquals(target.getRole(), "ROLE_USER");
    }

    @Test
    @WithAccount("testAdmin")
    @DisplayName("배송사 관리2")
    void deliveryManagement() throws Exception {
        mockMvc.perform(get("/admin/delivery/manage"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/delivery-manage"));

    }

    @Test
    @WithAccount("testAdmin")
    @DisplayName("결제 관리 폼")
    void paymentManageForm() throws Exception {
        mockMvc.perform(get("/admin/payment"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("orderList"))
                .andExpect(view().name("admin/payment-manage"));
    }

}
