package com.project.market.modules.account.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class ProfileForm {

    @NotEmpty
    @Length(min = 2, max = 20, message = "별명은 2~10자 사이여야 합니다.")
    private String nickname;

    private String phone;

    private String email;

    private String profileImage;

    private boolean itemEnrollAlertByWeb;
    private boolean itemEnrollAlertByMail;
}
