package com.project.market.modules.account.dao;

import com.project.market.WithAccount;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.form.ProfileForm;
import com.project.market.modules.account.form.SignupForm;
import com.project.market.modules.security.AccountContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest
class AccountServiceTest {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EntityManager em;

//    @BeforeEach
//    void beforeEach() {
//        Account account = Account.builder()
//                .username("testUserName")
//                .loginId("testLoginId")
//                .phone("010-5050-6060")
//                .email("test99@mail.com")
//                .nickname("testNickName")
//                .password(passwordEncoder.encode("testpass"))
//                .joinedAt(LocalDateTime.now())
//                .bio("남")
//                .role("ROLE_USER")
//                .build();
//        accountRepository.save(account);
//    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자 등록")
    void saveNewAccount() {
        //given
        SignupForm signupForm = new SignupForm();
        signupForm.setLoginId("user0908");
        signupForm.setPassword("pass0908");
        signupForm.setPhone("010-1298-0998");
        signupForm.setEmail("e9mail@mail.com");
        signupForm.setUsername("홍길동");
        //when
        accountService.saveNewAccount(signupForm);
        //then
        Account account = accountRepository.findByLoginId("user0908");
        Assertions.assertNotNull(account);
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("프로필 수정")
    void editProfile() {
        //given
        Account account = getCurrentAccount();
        ProfileForm profileForm = new ProfileForm();
        profileForm.setProfileImage("newImg");
        profileForm.setEmail("newMail@test.com");
        profileForm.setPhone("010-1298-1298");
        profileForm.setNickname("newNick");
        //when
        accountService.editProfile(account, profileForm);
        //then
        Account findAccount = getCurrentAccount();
        assertEquals(findAccount.getNickname(), "newNick");
        assertEquals(findAccount.getPhone(), "01012981298");
        assertEquals(findAccount.getEmail(), "newMail@test.com");
        assertEquals(findAccount.getProfileImage(), "newImg");
    }

    // mockito 사용할지 미정
//    @Test
//    @WithAccount("testUser")
//    @DisplayName("비밀번호 변경")
//    void modifyPassword() {
//        //given
//        Account account = getCurrentAccount();
//        String newPassword = "1q2w3e4r";
//        //when
//        accountService.modifyPassword(account, newPassword);
//        //then
//        Account findAccount = getCurrentAccount();
//        assertTrue(passwordEncoder.matches("1q2w3e4r", findAccount.getPassword()));
//    }

    Account getCurrentAccount() {
        AccountContext accountContext = (AccountContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return accountRepository.findById(accountContext.getAccount().getId()).orElseThrow();
    }
}
