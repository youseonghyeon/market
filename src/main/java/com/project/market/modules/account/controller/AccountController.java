package com.project.market.modules.account.controller;

import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.dao.AccountService;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.entity.AccountType;
import com.project.market.modules.account.form.SignupForm;
import com.project.market.modules.account.util.CurrentAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @GetMapping("/login")
    public String loginForm() {

        return "account/login";
    }

    @GetMapping("/sign-up")
    public String signupForm() {

        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signup(@Valid SignupForm signupForm, Errors errors) {
        if (errors.hasErrors()) {
            return "account/sign-up";
        }
        log.info("아이디={}, 비밀번호={}, 이메일={}", signupForm.getUsername(), signupForm.getPassword(), signupForm.getEmail());
        String encodedPW = passwordEncoder.encode(signupForm.getPassword());
        log.info("비밀번호 동일 확인={}", passwordEncoder.matches("1111", encodedPW));

        // TODO 아이디 중복 검증

        Account account = Account.builder()
                .name(signupForm.getUsername())
                .loginId(signupForm.getLoginId())
                .nickname(signupForm.getLoginId())
                .email(signupForm.getEmail())
                .password(encodedPW)
                .joinedAt(LocalDateTime.now())
                .accountType(AccountType.USER).build();

        // TODO account 등록

        return "redirect:/";
    }


    @GetMapping("/profile")
    public String profileForm(@CurrentAccount Account account) {

        return "account/profile";
    }
}
