package com.project.market;

import com.project.market.infra.TestUtils;
import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.account.service.AccountService;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.form.SignupForm;
import com.project.market.modules.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Locale;

public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TestUtils testUtils;

    public WithAccountSecurityContextFactory(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public SecurityContext createSecurityContext(WithAccount annotation) {
        String loginId = annotation.value();

        SignupForm signupForm = new SignupForm();
        signupForm.setLoginId(loginId);
        signupForm.setPassword("testpass");
        signupForm.setUsername("testname");
        signupForm.setEmail("email@email.com");
        signupForm.setPhone(testUtils.createRandomPhoneNumber());

        accountService.saveNewAccount(signupForm);
        Account account = accountRepository.findByLoginId(loginId);
        if (loginId.toLowerCase(Locale.ROOT).contains("admin")) {
            account.modifyRole("ROLE_ADMIN");
        } else if (loginId.toLowerCase(Locale.ROOT).contains("manager")) {
            account.modifyRole("ROLE_MANAGER");
        } else if (loginId.toLowerCase(Locale.ROOT).contains("courier")) {
            account.modifyRole("ROLE_COURIER");
        } else {
            account.modifyRole("ROLE_USER");
        }

        UserDetails principal = userDetailsService.loadUserByUsername(loginId);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
