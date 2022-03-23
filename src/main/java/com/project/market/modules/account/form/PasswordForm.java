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


/**
 *     public static final String REGEX_USER_ID    = "^(?=.*[a-zA-z])(?=.*[0-9])(?!.*[^a-zA-z0-9]).{5,20}$"; // 정규식 패턴 (사용자 ID - 영문자/숫자 포함 5~20자)
 *     public static final String REGEX_USER_PW    = "^(?=.*[a-zA-z])(?=.*[0-9])(?=.*[`~!@$!%*#^?&\\(\\)\\-_=+])(?!.*[^a-zA-z0-9`~!@$!%*#^?&\\(\\)\\-_=+]).{8,16}$"; // 정규식 패턴 (사용자 PW - 영문자/숫자/특수문자 포함 8~16자)
 */
