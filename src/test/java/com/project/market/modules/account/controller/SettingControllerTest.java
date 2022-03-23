package com.project.market.modules.account.controller;

import com.project.market.WithAccount;
import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.item.dao.TagRepository;
import com.project.market.modules.item.entity.Tag;
import com.project.market.modules.security.AccountContext;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @Autowired
    TagRepository tagRepository;

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

    @Test
    @WithAccount("testUser")
    @DisplayName("태그 추가 폼")
    void tagSettingForm() throws Exception {
        mockMvc.perform(get("/profile/tag"))
                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("tagList"))
                .andExpect(model().attributeExists("whiteList"))
                .andExpect(view().name("account/settings/tag"));
    }


    @Test
    @WithAccount("testUser")
    @DisplayName("태그 추가")
    void tagSetting() throws Exception {
        mockMvc.perform(post("/profile/tag")
                        .param("new-tag", "홈런볼")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/tag"));
        List<String> tags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        assertTrue(tags.contains("홈런볼"));
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

        Account findAccount = accountRepository.findByLoginId("testUser");
        assertNull(findAccount.getPasswordConfirmToken());
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("비밀번호 변경")
    void modifyPassword() throws Exception {

        mockMvc.perform(post("/help/modify/password")
                        .cookie(new Cookie("temp_email", "email@email.com"))
                        .param("new-password", "newpassword")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        Account account = accountRepository.findByLoginId("testUser");
        assertTrue(passwordEncoder.matches("newpassword", account.getPassword()));
    }

}
