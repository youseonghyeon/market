package com.project.market.modules.account.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class SignupForm {

    @NotEmpty
    @Length(min = 5, max = 15)
    private String loginId;

    @NotEmpty
    private String username;

    @NotEmpty
    @Length(min = 6, max = 30)
    private String password;

    @NotEmpty
    @Length(min = 10, max = 13)
    private String phone;

    private String email;


}
