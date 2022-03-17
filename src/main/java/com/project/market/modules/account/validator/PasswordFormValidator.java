package com.project.market.modules.account.validator;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.form.PasswordForm;
import com.project.market.modules.security.AccountContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class PasswordFormValidator implements Validator {

    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PasswordForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordForm passwordForm = (PasswordForm) target;
        AccountContext context = (AccountContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account account = context.getAccount();

        if (!passwordEncoder.matches(passwordForm.getPassword(), account.getPassword())) {
            errors.rejectValue("password", "mismatch.password", "비밀번호가 일치하지 않습니다.");
        }
        if (passwordForm.getNewPassword().equals(passwordForm.getPassword())) {
            errors.rejectValue("newPassword", "invalid.password", "새로운 비밀번호를 입력해주세요.");
        }

    }
}
