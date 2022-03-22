package com.project.market.modules.account.controller;

import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.dao.AccountService;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.form.PasswordForm;
import com.project.market.modules.account.form.ProfileForm;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.account.validator.PasswordFormValidator;
import com.project.market.modules.item.dao.TagRepository;
import com.project.market.modules.item.dao.TagService;
import com.project.market.modules.item.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SettingController {

    private final ModelMapper modelMapper;
    private final AccountService accountService;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final AccountRepository accountRepository;
    private final PasswordFormValidator passwordFormValidator;

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
    public String tagSetting() {
        // TODO 태그 추가 기능
        return null;
    }

}
