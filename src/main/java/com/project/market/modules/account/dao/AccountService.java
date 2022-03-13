package com.project.market.modules.account.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.entity.AccountType;
import com.project.market.modules.account.form.ProfileForm;
import com.project.market.modules.account.form.SignupForm;
import com.project.market.modules.account.util.PhoneUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

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
                .accountType(AccountType.USER)
                .build();

        accountRepository.save(account);
    }

    public void editProfile(Account account, ProfileForm profileForm) {
        account.modifyProfile(profileForm);
        accountRepository.save(account);
    }
}
