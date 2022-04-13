package com.project.market.modules.account.controller;

import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.dao.AccountService;
import com.project.market.modules.account.dao.ZoneRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.entity.Zone;
import com.project.market.modules.account.form.AddressForm;
import com.project.market.modules.account.form.PasswordForm;
import com.project.market.modules.account.form.ProfileForm;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.account.validator.PasswordFormValidator;
import com.project.market.modules.item.dao.repository.TagRepository;
import com.project.market.modules.item.dao.TagService;
import com.project.market.modules.item.dto.TagDto;
import com.project.market.modules.item.entity.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        List<Tag> tagList = accountService.findTags(account);
        List<Tag> whiteList = tagRepository.findTop20ByOrderByCountDesc();
        model.addAttribute("tagList", tagList);
        model.addAttribute("whiteList", whiteList);
        return "account/settings/tag";
    }

    @PostMapping("/profile/tag")
    @ResponseBody
    public void tagSetting(@CurrentAccount Account account, @RequestBody TagDto tagDto) {
        Tag findTag = tagService.createOrFindTag(tagDto.getNewTag());
        accountService.saveNewTag(account, findTag);
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
        accountService.saveNewZone(account, zone);
        return "redirect:/profile/zone";
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
}
