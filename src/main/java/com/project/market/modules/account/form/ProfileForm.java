package com.project.market.modules.account.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class ProfileForm {

    @NotEmpty
    @Length(min = 2, max = 10)
    private String nickname;

    @NotEmpty
    private String phone;

    private String email;

    private String profileImage;
}
