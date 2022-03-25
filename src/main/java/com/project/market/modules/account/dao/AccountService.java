package com.project.market.modules.account.dao;

import com.project.market.infra.mail.TokenMailSender;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.entity.Zone;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenMailSender tokenMailSender;

    public void saveNewAccount(SignupForm signupForm) {
        String encode = passwordEncoder.encode(signupForm.getPassword());
        String phone = PhoneUtils.trim(signupForm.getPhone());

        Account account = Account.builder()
                .username(signupForm.getUsername())
                .loginId(signupForm.getLoginId())
                .phone(phone)
                .email(signupForm.getEmail())
                .nickname(signupForm.getLoginId())
                .password(encode)
                .joinedAt(LocalDateTime.now())
                .role("ROLE_USER")
                .orders(new ArrayList<>())
                .enrolledItem(new ArrayList<>())
                .tags(new ArrayList<>())
                .zones(new ArrayList<>())
                .build();

        accountRepository.save(account);
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

    public String saveNewToken(Account account) {
        String token = createNewToken();
        account.savePasswordToken(token);
        accountRepository.save(account);
        return token;
    }

    public void sendTokenMail(Account account, String token) {
        tokenMailSender.send(account.getEmail(), token);
    }

    private String createNewToken() {
        return UUID.randomUUID().toString();
    }

    public void destroyPasswordToken(Account account) {
        account.savePasswordToken(null);
    }

}
