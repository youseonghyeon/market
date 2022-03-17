package com.project.market.modules.account.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PasswordForm {
    // TODO 정규식 검증을 사용해주세요.
    @Length(min = 6, max = 30, message = "8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;

    @Length(min = 6, max = 30, message = "8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String newPassword;
}
