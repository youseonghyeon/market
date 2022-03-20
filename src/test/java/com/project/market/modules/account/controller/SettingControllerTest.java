package com.project.market.modules.account.controller;

import com.project.market.WithAccount;
import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.entity.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
class SettingControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    public void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    void passwordInitBinder() {

    }

    @Test
    @WithAccount("testUser")
    @DisplayName("프로필 수정 폼")
    void profileEditForm() throws Exception {
        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("profileForm"))
                .andExpect(view().name("account/settings/profile-edit"));
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("프로필 수정")
    void profileEdit() throws Exception {
        mockMvc.perform(post("/profile/edit")
                        .param("nickname", "PickMe")
                        .param("phone", "01011882299")
                        .param("email", "testmodify@mail.com")
                        .param("profileImage", "")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("message"));

        Account findAccount = accountRepository.findByLoginId("testUser");
        assertEquals(findAccount.getNickname(), "PickMe");
        assertEquals(findAccount.getPhone(), "01011882299");
        assertEquals(findAccount.getEmail(), "testmodify@mail.com");
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("비밀번호 수정 폼")
    void passwordModifyForm() throws Exception {
        mockMvc.perform(get("/password"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(view().name("account/settings/password"));
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("비밀번호 수정")
    void passwordModify() throws Exception {
        mockMvc.perform(post("/password")
                        .param("password", "testpass")
                        .param("newPassword", "modifiedPass")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        Account findAccount = accountRepository.findByLoginId("testUser");
        assertTrue(passwordEncoder.matches("modifiedPass", findAccount.getPassword()));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}