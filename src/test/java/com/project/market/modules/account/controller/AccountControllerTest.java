package com.project.market.modules.account.controller;

import com.project.market.WithAccount;
import com.project.market.infra.MockAccount;
import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.account.service.AccountService;
import com.project.market.modules.account.entity.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
    MockMvc mockMvc;
    @Autowired
    MockAccount mockAccount;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;


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

        String newLoginId = "user99";

        // 계정 생성 (비밀번호 = MockAccount.ACCOUNT_PASSWORD)
        mockAccount.createMockAccount(newLoginId);


        mockMvc.perform(post("/perform_login")
                        .param("loginId", newLoginId)
                        .param("password", MockAccount.ACCOUNT_PASSWORD)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("로그인 실패(존재하지 않는 회원)")
    void loginFail() throws Exception {
        String newLoginId = "user99";
        String exLoginId = "userxxx";

        // 계정 생성 (비밀번호 = MockAccount.ACCOUNT_PASSWORD)
        mockAccount.createMockAccount(newLoginId);

        mockMvc.perform(post("/perform_login")
                        .param("loginId", exLoginId)
                        .param("password", MockAccount.ACCOUNT_PASSWORD)
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
        String loginId = "user1000";
        String password = "pass1000";
        String username = "홍길동";
        String email = "gildong@mail.com";
        String phone = "010-1010-2020";
        String phoneTrim = "01010102020";

        mockMvc.perform(post("/sign-up")
                        .param("loginId", loginId)
                        .param("password", password)
                        .param("username", username)
                        .param("email", email)
                        .param("phone", phone)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        Account account = accountRepository.findByLoginId(loginId);
        assertEquals(account.getLoginId(), loginId);
        assertEquals(account.getPhone(), phoneTrim);
        assertEquals(account.getEmail(), email);
        assertTrue(passwordEncoder.matches(password, account.getPassword()));
        assertEquals(account.getNickname(), loginId);
        assertNotNull(account.getJoinedAt());
        assertEquals(account.getRole(), "ROLE_USER");
        assertNull(account.getPasswordToken());
        assertNull(account.getPasswordTokenCreatedAt());
        assertNull(account.getZoneCode());
        assertNull(account.getRoadAddress());
        assertNull(account.getAddressDetail());
        assertEquals(account.getCreditScore(), 0);
        assertTrue(account.isItemEnrollAlertByWeb());
        assertFalse(account.isItemEnrollAlertByMail());
        assertFalse(account.isDeleted());
    }

    @Test
    @DisplayName("회원가입 실패 - loginId 길이 부족")
    void signupFail() throws Exception {
        // 아이디 길이 부적합 5 ~ 15
        String loginEx = "user";
        String password = "pass1000";
        String username = "홍길동";
        String email = "gildong@mail.com";
        String phone = "010-1010-2020";

        mockMvc.perform(post("/sign-up")
                        .param("loginId", loginEx)
                        .param("password", password)
                        .param("username", username)
                        .param("email", email)
                        .param("phone", phone)
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
}
