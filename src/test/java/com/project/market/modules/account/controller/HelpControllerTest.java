package com.project.market.modules.account.controller;

import com.project.market.WithAccount;
import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.dao.AccountService;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.dao.repository.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class HelpControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    TagRepository tagRepository;

    @AfterEach
    public void afterEach() {
        accountRepository.deleteAll();
    }


    @Test
    @WithAccount("testUser")
    @DisplayName("비밀번호 찾기 폼")
    void findPasswordForm() throws Exception {
        mockMvc.perform(get("/help/find-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/help/find-password"));
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("토큰 메일 전송 성공")
    void sendTokenMail() throws Exception {
        mockMvc.perform(post("/help/find-password")
                        .param("loginId", "admin")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/help/send-token"))
                .andExpect(cookie().value("temp_loginId", "admin"));
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("토큰 메일 전송 실패(유효하지 않은 아이디)")
    void sendTokenMailFail() throws Exception {
        mockMvc.perform(post("/help/find-password")
                        .param("loginId", "aaaaaa")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name("account/help/find-password"));
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("메일 전송 성공 폼")
    void completeForm() throws Exception {
        mockMvc.perform(get("/help/send-token"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/help/success"));
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("토큰 인증")
    void tokenCertification() throws Exception {
        Account account = accountRepository.findByLoginId("testUser");
        String token = accountService.createPasswordToken(account);

        mockMvc.perform(get("/help/confirm")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(view().name("account/help/modify-password"));
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("비밀번호 변경")
    void modifyPassword() throws Exception {
        Account account = accountRepository.findByLoginId("testUser");
        String token = accountService.createPasswordToken(account);
        mockMvc.perform(post("/help/modify/password")
                        .cookie(new Cookie("temp_loginId", account.getLoginId()))
                        .cookie(new Cookie("temp_token", token))
                        .param("new-password", "newpassword")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        assertNull(account.getPasswordToken());
    }

}
