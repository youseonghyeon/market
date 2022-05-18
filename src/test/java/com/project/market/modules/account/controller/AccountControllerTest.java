package com.project.market.modules.account.controller;

import com.project.market.WithAccount;
import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.account.service.AccountService;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.form.SignupForm;
import org.junit.jupiter.api.AfterEach;
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
class AccountControllerTest {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @AfterEach
    public void clearAccount() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인 폼")
    void loginForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/login"));
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() throws Exception {
        // 계정 생성
        joinAccount("user1234", "pass1234");

        mockMvc.perform(post("/perform_login")
                        .param("loginId", "user1234")
                        .param("password", "pass1234")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("로그인 실패(존재하지 않는 회원)")
    void loginFail() throws Exception {
        mockMvc.perform(post("/perform_login")
                        .param("loginId", "notExistsUser")
                        .param("password", "notExistsPassword")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("로그아웃")
    void logout() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("회원가입 폼")
    void signupForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signupForm"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("loginId", "user0099")
                        .param("password", "pass0099")
                        .param("username", "유성현")
                        .param("email", "email@mail.com")
                        .param("phone", "010-6060-4040")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        Account account = accountRepository.findByLoginId("user0099");
        assertEquals(account.getNickname(), "user0099");
        assertEquals(account.getLoginId(), "user0099");
        assertEquals(account.getPhone(), "01060604040");
        assertEquals(account.getEmail(), "email@mail.com");
        assertTrue(passwordEncoder.matches("pass0099", account.getPassword()));
        assertNotNull(account.getJoinedAt());
        assertEquals(account.getRole(), "ROLE_USER");
    }

    @Test
    @DisplayName("회원가입 실패")
    void signupFail() throws Exception {
        // 아이디 길이 부적합
        mockMvc.perform(post("/sign-up")
                        .param("loginId", "u")
                        .param("password", "password")
                        .param("username", "youseonghyeon")
                        .param("email", "email@mail.com")
                        .param("phone", "01012341234")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signupForm"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("프로필 폼")
    void profileForm() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name("account/profile"));
    }

    private void joinAccount(String loginId, String password) {
        SignupForm signupForm = new SignupForm();
        signupForm.setLoginId(loginId);
        signupForm.setPassword(password);
        signupForm.setUsername("유성현");
        signupForm.setEmail("email@email.com");
        signupForm.setPhone("010-1212-3434");
        accountService.saveNewAccount(signupForm);
    }
}
