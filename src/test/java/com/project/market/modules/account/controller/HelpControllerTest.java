package com.project.market.modules.account.controller;

import com.project.market.WithAccount;
import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.dao.TagRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class HelpControllerTest {

    @Autowired
    MockMvc mockMvc;
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
    @DisplayName("토큰 메일 전송")
    void sendTokenMail() throws Exception {
        mockMvc.perform(post("/help/find-password")
                        .param("email", "email@email.com")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/help/send-token"))
                .andExpect(cookie().value("temp_email", "email@email.com"));
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
    @DisplayName("메일에서 입력했을 때 비밀번호 변경 폼")
    void tokenCertification() throws Exception {
        Account account = accountRepository.findByLoginId("testUser");
        String uuid = UUID.randomUUID().toString();
        account.savePasswordToken(uuid);

        mockMvc.perform(get("/help/confirm")
                        .param("token", uuid))
                .andExpect(status().isOk())
                .andExpect(view().name("account/help/modify-password"));

    }

    @Test
    @WithAccount("testUser")
    @DisplayName("비밀번호 변경")
    void modifyPassword() throws Exception {
        // 재 작성
//        Account account = accountRepository.findByLoginId("testUser");
//
//        mockMvc.perform(post("/help/modify/password")
//                        .cookie(new Cookie("temp_email", "email@email.com"))
//                        .cookie(new Cookie("temp_token", ))
//                        .param("new-password", "newpassword")
//                        .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/login"));
//
//        Account account = accountRepository.findByLoginId("testUser");
//        assertTrue(passwordEncoder.matches("newpassword", account.getPassword()));
    }


//    @Test
//    void findPasswordForm() {
//    }
//
//    @Test
//    void sendMail() {
//    }
//
//    @Test
//    void completeForm() {
//    }
//
//    @Test
//    void tokenCertification() {
//    }
//
//    @Test
//    void modifyPassword() {
//    }
}
