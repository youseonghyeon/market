package com.project.market.infra;

import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.dao.AccountService;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.form.SignupForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@Component
public class MockAccount {


    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;

    public static String ACCOUNT_PASSWORD = "password1234";
    public static String USER_NAME = "홍길동";
    public static String EMAIL = "email@email.com";


    public Account createMockAccount(String loginId) {
        SignupForm form = createSignupForm(loginId);
        accountService.saveNewAccount(form);
        return accountRepository.findByLoginId(form.getLoginId());
    }

    private SignupForm createSignupForm(String loginId) {
        SignupForm form = new SignupForm();
        form.setLoginId(loginId);
        form.setPassword(ACCOUNT_PASSWORD);
        form.setUsername(USER_NAME);
        form.setEmail(EMAIL);
        form.setPhone(createRandomPhoneNumber());
        return form;
    }

    public String createRandomPhoneNumber() {
        double number = Math.random();
        String s = Double.toString(number);
        return "010" + s.substring(2, 10);
    }

}
