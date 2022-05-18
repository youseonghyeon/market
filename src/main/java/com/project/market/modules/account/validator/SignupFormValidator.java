package com.project.market.modules.account.validator;

import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.account.form.SignupForm;
import com.project.market.modules.account.util.PhoneUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignupFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignupForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignupForm signupForm = (SignupForm) target;
        if (accountRepository.existsByLoginId(signupForm.getLoginId())) {
            errors.rejectValue("loginId", "invalid.loginId", "이미 사용중인 아이디입니다.");
        }

        String phone = PhoneUtils.trim(signupForm.getPhone());

        if (accountRepository.existsByPhone(phone)) {
            errors.rejectValue("phone", "invalid.phone", "이미 등록된 전화번호입니다.");
        }
    }
}
