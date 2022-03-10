package com.project.market.modules.account.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SignupForm {

    @NotEmpty
    private String username;

    // TODO html에 항목 추가하기
    private String loginId;

    @NotEmpty
    private String password;

    @NotEmpty
    private String email;

}
