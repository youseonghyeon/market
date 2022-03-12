package com.project.market.modules.account.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.form.SignupForm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class AccountServiceTest {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;

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
}
