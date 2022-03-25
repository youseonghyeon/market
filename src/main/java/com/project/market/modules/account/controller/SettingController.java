package com.project.market.modules.account.controller;

import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.dao.AccountService;
import com.project.market.modules.account.dao.ZoneRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.entity.Zone;
import com.project.market.modules.account.form.PasswordForm;
import com.project.market.modules.account.form.ProfileForm;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.account.validator.PasswordFormValidator;
import com.project.market.modules.item.dao.TagRepository;
import com.project.market.modules.item.dao.TagService;
import com.project.market.modules.item.entity.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SettingController {

    private final ModelMapper modelMapper;
    private final AccountService accountService;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final AccountRepository accountRepository;
    private final PasswordFormValidator passwordFormValidator;
    private final ZoneRepository zoneRepository;

    @InitBinder("passwordForm")
    public void passwordInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(passwordFormValidator);
    }

    @GetMapping("/profile/edit")
    public String profileEditForm(@CurrentAccount Account account, Model model) {
        ProfileForm profileForm = modelMapper.map(account, ProfileForm.class);
        model.addAttribute(profileForm);
        return "account/settings/profile-edit";
    }

    @PostMapping("/profile/edit")
    public String profileEdit(@CurrentAccount Account account, @Valid ProfileForm profileForm,
                              Errors errors, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            return "account/settings/profile-edit";
        }
        accountService.editProfile(account, profileForm);
        attributes.addFlashAttribute("message", "수정 완료!");
        return "redirect:/profile";
    }

    @GetMapping("/password")
    public String passwordModifyForm(Model model) {
        model.addAttribute(new PasswordForm());
        return "account/settings/password";
    }

    @PostMapping("/password")
    public String passwordModify(@CurrentAccount Account account,
                                 @Valid PasswordForm passwordForm,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 Errors errors) {
        if (errors.hasErrors()) {
            return "/account/settings/password";
        }
        // PasswordFormValidator 실행
        accountService.modifyPassword(account, passwordForm.getNewPassword());
        accountService.logout(request, response);
        return "redirect:/login";
    }

    @GetMapping("/profile/tag")
    public String tagSettingForm(@CurrentAccount Account account, Model model) {
        List<Tag> tagList = accountService.findTags(account);
        List<Tag> whiteList = tagRepository.findTop20ByOrderByCountDesc();
        model.addAttribute("tagList", tagList);
        model.addAttribute("whiteList", whiteList);
        return "account/settings/tag";
    }

    @PostMapping("/profile/tag")
    public String tagSetting(@CurrentAccount Account account, @RequestParam("new-tag") String tag) {
        Tag findTag = tagService.findOrCreateTag(tag);
        accountService.saveNewTag(account, findTag);
        return "redirect:/profile/tag";
    }


    @GetMapping("/help/find-password")
    public String findPasswordForm() {
        return "account/help/find-password";
    }

    @PostMapping("/help/find-password")
    public String sendMail(@RequestParam("email") String email,
                           HttpServletResponse response,
                           RedirectAttributes attributes) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            attributes.addAttribute("errorMsg", "error");
            return "account/help/find-password";
        }
        String token = accountService.saveNewToken(account);
        accountService.sendTokenMail(account, token);
        Cookie emailCookie = new Cookie("temp_email", email);
        Cookie tokenCookie = new Cookie("temp_token", token);
        emailCookie.setMaxAge(300);
        tokenCookie.setMaxAge(300);
        response.addCookie(emailCookie);
        response.addCookie(tokenCookie);

        return "redirect:/help/send-token";
    }

    @GetMapping("/help/send-token")
    public String completeForm() {
        return "account/help/success";
    }

    @GetMapping("/help/confirm")
    public String tokenCertification(@RequestParam("token") String token) {
        Account account = accountRepository.findByPasswordConfirmToken(token);
        if (account == null) {
            return "account/help/fail";
        }
        if (account.isExpiredPasswordToken()) {
            return "account/help/fail";
        }
        return "account/help/modify-password";
    }

    @PostMapping("/help/modify/password")
    public String modifyPassword(@CookieValue(value = "temp_email") Cookie emailCookie,
                                 @CookieValue(value = "temp_token") Cookie tokenCookie,
                                 @RequestParam("new-password") String password,
                                 HttpServletResponse response) {
        Account account = accountRepository.findByEmail(emailCookie.getValue());
        if (account == null || !isValidPasswordToken(account, tokenCookie.getValue())) {
            return "account/help/fail";
        }

        accountService.modifyPassword(account, password);
        accountService.destroyPasswordToken(account);
        emailCookie.setValue(null);
        tokenCookie.setValue(null);
        emailCookie.setMaxAge(0);
        tokenCookie.setMaxAge(0);
        response.addCookie(emailCookie);
        response.addCookie(tokenCookie);
        return "redirect:/login";
    }

    private boolean isValidPasswordToken(Account account, String token) {
        return account.getPasswordConfirmToken().equals(token) && !account.isExpiredPasswordToken();
    }

    @GetMapping("/profile/zone")
    public String zoneSettingForm(@CurrentAccount Account account, Model model) {
        Account findAccount = accountRepository.findAccountWithZonesById(account.getId());
        List<Zone> whiteList = zoneRepository.findAll();
        model.addAttribute("zoneList", findAccount.getZones());
        model.addAttribute("whiteList", whiteList);
        return "account/settings/zone";
    }

    @PostMapping("/profile/zone")
    public String zoneSetting(@CurrentAccount Account account, @RequestParam("new-zone") String city) {
//        if (errors.hasErrors()) {
//            return "account/settings/zone";
//        }
        // TODO text입력이 아닌 리스트에서 고르는걸로 바꿀것
        Zone zone = zoneRepository.findByCity(city);
        if (zone == null) {
            return "account/settings/zone";
        }
        accountService.saveNewZone(account,zone);
        return "redirect:/profile/zone";
    }
}
