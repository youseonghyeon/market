package com.project.market.modules.account.controller;

import com.project.market.infra.MockAccount;
import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.account.service.AccountService;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.repository.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    MockAccount mockAccount;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    TagRepository tagRepository;

    @AfterEach
    public void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("아이디 찾기 폼")
    void findLonginIdForm() throws Exception {
        mockMvc.perform(get("/help/find-id"))
                .andExpect(view().name("account/help/find-id"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("아이디 찾기")
    void findLoginId() throws Exception {
        Account account = mockAccount.createMockAccount("user12");

        mockMvc.perform(post("/help/find-id")
                        .param("email", account.getEmail())
                        .with(csrf()))
                .andExpect(view().name("account/help/login-id"))
                .andExpect(model().attribute("id", account.getLoginId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("아이디 찾기 실패(회원 정보 없음)")
    void findLoginIdEx() throws Exception {
        String emailEx = "xxxx@xxxxxx.xxx";

        mockMvc.perform(post("/help/find-id")
                        .param("email", emailEx)
                        .with(csrf()))
                .andExpect(redirectedUrl("/help/find-id"))
                .andExpect(flash().attribute("message", "등록된 회원이 존재하지 않습니다."))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("비밀번호 찾기 폼")
    void findPasswordForm() throws Exception {
        mockMvc.perform(get("/help/find-password"))
                .andExpect(view().name("account/help/find-password"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("토큰 메일 전송 성공")
    void sendTokenMail() throws Exception {
        mockMvc.perform(post("/help/find-password")
                        .param("loginId", "admin")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/help/send-token"));
    }

    @Test
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
    @DisplayName("메일 전송 성공 폼")
    void completeForm() throws Exception {
        mockMvc.perform(get("/help/send-token"))
                .andExpect(view().name("account/help/success"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("토큰 인증")
    void tokenCertification() throws Exception {
        Account account = mockAccount.createMockAccount("testUser");
        String token = accountService.createPasswordToken(account);

        mockMvc.perform(get("/help/confirm")
                        .param("token", token))
                .andExpect(view().name("account/help/modify-password"))
                .andExpect(model().attribute("loginId", account.getLoginId()))
                .andExpect(model().attribute("token", token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("비밀번호 변경")
    void modifyPassword() throws Exception {
        Account account = mockAccount.createMockAccount("testUser");
        String token = accountService.createPasswordToken(account);
        String newPassword = "newPass000";

        mockMvc.perform(post("/help/modify/password")
                        .param("loginId", account.getLoginId())
                        .param("token", token)
                        .param("new-password", newPassword)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        assertNull(account.getPasswordToken());
        passwordEncoder.matches(newPassword, account.getPassword());
    }

}
