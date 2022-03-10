package com.project.market.modules.account.controller;

import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.dao.AccountService;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.form.SignupForm;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.account.validator.SignupFormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final SignupFormValidator signupFormValidator;

    @InitBinder("signupForm")
    public void validateSignupForm(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signupFormValidator);
    }


    @GetMapping("/login")
    public String loginForm() {
        return "account/login";
    }

    @GetMapping("/sign-up")
    public String signupForm(Model model) {
        model.addAttribute(new SignupForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signup(@Valid SignupForm signupForm, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(signupForm);
            return "account/sign-up";
        }
        accountService.saveNewAccount(signupForm);
        return "redirect:/";
    }


    @GetMapping("/profile")
    public String profileForm(@CurrentAccount Account account) {

        return "account/profile";
    }
}
