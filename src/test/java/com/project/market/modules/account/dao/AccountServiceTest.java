package com.project.market.modules.account.dao;

import com.project.market.WithAccount;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.form.ProfileForm;
import com.project.market.modules.account.form.SignupForm;
import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.account.service.AccountService;
import com.project.market.modules.item.repository.TagRepository;
import com.project.market.modules.item.entity.Tag;
import com.project.market.modules.security.AccountContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
    private TagRepository tagRepository;

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
        assertNotNull(account);
        assertEquals(account.getLoginId(), "user0908");
        assertTrue(passwordEncoder.matches("pass0908", account.getPassword()));
        assertEquals(account.getPhone(), "01012980998");
        assertEquals(account.getEmail(), "e9mail@mail.com");
        assertEquals(account.getNickname(), "user0908");
        assertEquals(account.getRole(), "ROLE_USER");
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("프로필 수정")
    void editProfile() {
        //given
        Account account = getCurrentAccount();
        ProfileForm profileForm = new ProfileForm();
        profileForm.setNickname("newNick");
        profileForm.setPhone("01012981298");
        profileForm.setEmail("newMail@test.com");
        //when
        accountService.editProfile(account, profileForm);
        //then
        Account findAccount = accountRepository.findByLoginId("testUser");
        assertNotNull(findAccount);
        assertEquals(findAccount.getNickname(), "newNick");
        assertEquals(findAccount.getPhone(), "01012981298");
        assertEquals(findAccount.getEmail(), "newMail@test.com");
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("비밀번호 변경")
    void modifyPassword() {
        //given
        Account account = getCurrentAccount();
        String newPW = "asdf813y4he";
        //when
        accountService.modifyPassword(account, newPW);
        //then
        Account findAccount = accountRepository.findByLoginId("testUser");
        assertTrue(passwordEncoder.matches(newPW, findAccount.getPassword()));
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("태그 추가")
    void saveNewTag() {
        //given
        Account account = accountRepository.findByLoginId("testUser");
        Tag tag = new Tag("태그추가1", 1);
        tagRepository.save(tag);
        //when
        accountService.saveNewTag(account, tag);
        //then
        Account findAccount = accountRepository.findAccountWithTagsById(account.getId());
        assertTrue(findAccount.getTags().contains(tag));
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("태그 조회")
    void findTags() {
        //given
        Account account = accountRepository.findByLoginId("testUser");
        Tag tag1 = new Tag("태그조회1", 1);
        Tag tag2 = new Tag("태그조회2", 1);
        Tag tag3 = new Tag("태그조회3", 1);
        tagRepository.save(tag1);
        tagRepository.save(tag2);
        tagRepository.save(tag3);
        accountService.saveNewTag(account, tag1);
        accountService.saveNewTag(account, tag2);
        accountService.saveNewTag(account, tag3);
        //when
        List<Tag> tags = accountService.findTags(account);
        //then
        List<Tag> collection = new ArrayList<>();
        collection.add(tag1);
        collection.add(tag2);
        collection.add(tag3);
        assertEquals(tags.size(), 3);
        assertTrue(tags.containsAll(collection));
    }


    @Test
    @DisplayName("비밀번호 토큰 생성")
    void createPasswordToken() {
        //given
        회원가입("tokenUser", "tokenpass");
        Account account = accountRepository.findByLoginId("tokenUser");
        //when
        String token = accountService.createPasswordToken(account);
        //then
        assertEquals(account.getPasswordToken(), token);
        assertTrue(tokenValidation(account, token));
    }

    private void 회원가입(String loginId, String password) {
        SignupForm signupForm = new SignupForm();
        signupForm.setLoginId(loginId);
        signupForm.setPassword(password);
        signupForm.setUsername("유성현");
        signupForm.setEmail("email@email.com");
        signupForm.setPhone("010-1212-3434");
        accountService.saveNewAccount(signupForm);
    }


    private Account getCurrentAccount() {
        AccountContext accountContext = (AccountContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return accountRepository.findById(accountContext.getAccount().getId()).orElseThrow();
    }

    private boolean tokenValidation(Account account, String token) {
        return account != null &&
                account.getPasswordToken().equals(token) &&
                account.getPasswordTokenCreatedAt()
                        .isAfter(LocalDateTime.now().minusSeconds(600));

    }

}
