package com.project.market.modules.account.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class SignupForm {

    // 문자하나 숫자하나 정규식 ->  ^(?=.*[A-Za-z])(?=.*d)[A-Za-zd]{8,}$

    @NotEmpty(message = "필수 정보입니다.")
    @Length(min = 5, max = 15, message = "5~20자의 영문 소문자, 숫자와 특수기호(_),(-)만 사용 가능합니다.")
    private String loginId;

    @NotEmpty(message = "필수 정보입니다.")
    private String username;

    @NotEmpty(message = "필수 정보입니다.")
    @Length(min = 6, max = 30, message = "8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;

    @NotEmpty(message = "필수 정보입니다.")
    @Length(min = 10, max = 13)
    private String phone;

    private String email;


}
