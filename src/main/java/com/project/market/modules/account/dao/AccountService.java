package com.project.market.modules.account.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.entity.Zone;
import com.project.market.modules.account.form.AddressForm;
import com.project.market.modules.account.form.ProfileForm;
import com.project.market.modules.account.form.SignupForm;
import com.project.market.modules.account.util.PhoneUtils;
import com.project.market.modules.item.entity.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public void saveNewAccount(SignupForm signupForm) {
        Account account = newAccountBuild(signupForm);
        accountRepository.save(account);
    }

    private Account newAccountBuild(SignupForm signupForm) {
        return Account.builder()
                .username(signupForm.getUsername())
                .loginId(signupForm.getLoginId())
                .phone(PhoneUtils.trim(signupForm.getPhone()))
                .email(signupForm.getEmail())
                .nickname(signupForm.getLoginId())
                .password(passwordEncoder.encode(signupForm.getPassword()))
                .joinedAt(LocalDateTime.now())
                .role("ROLE_USER")
                .orders(new ArrayList<>())
                .enrolledItem(new ArrayList<>())
                .tags(new ArrayList<>())
                .zones(new ArrayList<>())
                .itemEnrollAlertByWeb(true)
                .itemEnrollAlertByMail(false)
                .build();
    }

    public void editProfile(Account account, ProfileForm profileForm) {
        account.modifyProfile(profileForm);
        accountRepository.save(account);
    }

    public void modifyPassword(Account account, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        account.modifyPassword(encodedPassword);
        accountRepository.save(account);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
    }

    @Transactional(readOnly = true)
    public List<Tag> findTags(Account account) {
        Account findAccount = accountRepository.findAccountWithTagsById(account.getId());
        return findAccount.getTags();
    }

    @Transactional(readOnly = true)
    public List<Zone> findZones(Account account) {
        Account findAccount = accountRepository.findAccountWithZonesById(account.getId());
        return findAccount.getZones();
    }

    public void saveNewTag(Account account, Tag tag) {
        Account findAccount = accountRepository.findAccountWithTagsById(account.getId());
        // TODO ##ERROR## 의도하지 않은 delete쿼리문이 나감 && 중복제거 해야함
        findAccount.getTags().add(tag);
    }

    public void saveNewZone(Account account, Zone zone) {
        Account findAccount = accountRepository.findAccountWithZonesById(account.getId());
        // TODO ##ERROR## 의도하지 않은 delete쿼리문이 나감 && 중복제거 해야함
        findAccount.getZones().add(zone);
    }

    public String createPasswordToken(Account account) {
        String token = createNewPasswordToken();
        account.savePasswordToken(token);
        accountRepository.save(account);
        return token;
    }

    private String createNewPasswordToken() {
        return UUID.randomUUID().toString();
    }

    public void destroyPasswordToken(Account account) {
        account.savePasswordToken(null);
    }

    public void modifyRole(Account account, String role) {
        account.modifyRole(role);
    }

    public void modifyAddress(Account account, AddressForm addressForm) {
        account.modifyAddress(addressForm);
        accountRepository.save(account);
    }
}
