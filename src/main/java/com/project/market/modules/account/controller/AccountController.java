package com.project.market.modules.account.controller;

import com.project.market.modules.account.dao.AccountService;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.form.SignupForm;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.account.validator.SignupFormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final SignupFormValidator signupFormValidator;

    @InitBinder("signupForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signupFormValidator);
    }

    @GetMapping("/login")
    public String loginForm() {
        return "account/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
//            SecurityContextHolder.getContext().setAuthentication(null);
            // TODO remember-me 사용 시 logout 불가능
        }
        return "redirect:/";
    }

    @GetMapping("/sign-up")
    public String signupForm(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signup(@Valid SignupForm signupForm, Errors errors) {
        if (errors.hasErrors()) {
            return "account/sign-up";
        }
        accountService.saveNewAccount(signupForm);
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profileForm(@CurrentAccount Account account, Model model) {
        model.addAttribute("account", account);
        return "account/profile";
    }


}
