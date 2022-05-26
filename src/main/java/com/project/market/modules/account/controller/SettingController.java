package com.project.market.modules.account.controller;

import com.project.market.infra.fileupload.AwsS3Service;
import com.project.market.modules.account.dto.ModifyImageReq;
import com.project.market.modules.account.dto.ModifyImageRes;
import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.account.service.AccountService;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.form.AddressForm;
import com.project.market.modules.account.form.PasswordForm;
import com.project.market.modules.account.form.ProfileForm;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.account.validator.PasswordFormValidator;
import com.project.market.modules.item.service.TagService;
import com.project.market.modules.item.repository.TagRepository;
import com.project.market.modules.item.dto.TagDto;
import com.project.market.modules.item.entity.Tag;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

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
    private final PasswordEncoder passwordEncoder;
    private final AwsS3Service awsS3Service;

    @InitBinder("passwordForm")
    public void passwordInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(passwordFormValidator);
    }

    @GetMapping("/profile/edit")
    public String profileEditForm(@CurrentAccount Account account, Model model) {
        ProfileForm profileForm = modelMapper.map(account, ProfileForm.class);
        model.addAttribute("profileForm", profileForm);
        return "account/settings/profile-edit";
    }

    @PostMapping("/profile/edit")
    public String profileEdit(@CurrentAccount Account account, @Valid ProfileForm profileForm,
                              Errors errors, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            return "account/settings/profile-edit";
        }
        accountService.editProfile(account, profileForm);
        attributes.addFlashAttribute("message", "프로필 수정 완료!");
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
        // 로그 아웃
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login";
    }

    @GetMapping("/profile/tag")
    public String tagSettingForm(@CurrentAccount Account account, Model model) {
        Set<Tag> tagList = accountService.findTags(account);
        List<Tag> whiteList = tagRepository.findTop20ByOrderByCountDesc();
        model.addAttribute("tagList", tagList);
        model.addAttribute("whiteList", whiteList);
        return "account/settings/tag";
    }

    @PostMapping("/profile/tag/add")
    @ResponseBody
    public void tagSetting(@CurrentAccount Account account, @RequestBody TagDto tagDto) {
        Tag findTag = tagService.createOrFindTag(tagDto.getTag());

        Account findAccount = accountRepository.findAccountWithTagsById(account.getId());
        accountService.saveNewTag(findAccount, findTag);
    }

    @PostMapping("/profile/tag/delete")
    @ResponseBody
    public void deleteTag(@CurrentAccount Account account, @RequestBody TagDto tagDto) {
        Tag tag = tagRepository.findByTitle(tagDto.getTag());
        Account findAccount = accountRepository.findById(account.getId()).orElseThrow();
        accountService.deleteTag(findAccount, tag);
    }



    @GetMapping("/profile/address")
    public String addressSettingForm(@CurrentAccount Account account, Model model) {
        model.addAttribute("addressForm", modelMapper.map(account, AddressForm.class));
        return "account/settings/address";
    }

    @PostMapping("/profile/address")
    public String addressSetting(@CurrentAccount Account account, @Valid AddressForm addressForm, RedirectAttributes attributes) {
        accountService.modifyAddress(account, addressForm);
        attributes.addFlashAttribute("message", "주소 변경 완료!");
        return "redirect:/profile";
    }

    @GetMapping("/account/withdrawal")
    public String accountWithdrawalForm() {
        return "account/settings/withdrawal";
    }

    @PostMapping("/account/withdrawal")
    public String accountWithdrawal(@CurrentAccount Account account, @RequestParam("pw-check") String password) {
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new IllegalStateException("비밀번호가 맞지 않습니다.");
        }
        accountService.deleteAccount(account);
        return "redirect:/";
    }

    @PostMapping("/profile/image")
    @ResponseBody
    public ModifyImageRes modifyProfileImage(@CurrentAccount Account account, @ModelAttribute ModifyImageReq req) {
        Account findAccount = accountRepository.findById(account.getId()).orElseThrow();
        accountService.modifyProfileImageAndNickName(findAccount, req.getNickname(), req.getProfileImage());

        return new ModifyImageRes(findAccount.getNickname(), findAccount.getProfileImagePath());
    }
}
