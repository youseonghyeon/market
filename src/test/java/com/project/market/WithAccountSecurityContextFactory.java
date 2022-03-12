package com.project.market;

import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.dao.AccountService;
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

public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;

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
        signupForm.setPhone("010-9900-9900");

        accountService.saveNewAccount(signupForm);
        UserDetails principal = userDetailsService.loadUserByUsername(loginId);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
