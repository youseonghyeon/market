package com.project.market.modules.account.controller;

import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.dao.AccountService;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.form.ProfileForm;
import com.project.market.modules.account.form.SignupForm;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.account.validator.SignupFormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    public void initBinder(WebDataBinder webDataBinder) {
        // 회원가입 폼 검증
        webDataBinder.addValidators(signupFormValidator);
    }


    @GetMapping("/login")
    public String loginForm() {
        return "account/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/";
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
    public String profileForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        return "account/profile";
    }

    @GetMapping("/profile/edit")
    public String profileEditForm(@CurrentAccount Account account, Model model) {
        ProfileForm profileForm = modelMapper.map(account, ProfileForm.class);
        model.addAttribute(profileForm);
        return "settings/account";
    }

    @PostMapping("/profile/edit")
    public String profileEdit(@CurrentAccount Account account, @Valid ProfileForm profileForm,
                              Errors errors, Model model) {
        // TODO 프로필 수정 관리 (및 settings/account에 attribute 변경했으므로 html 수정[account -> profileForm])
        return null;
    }
}
