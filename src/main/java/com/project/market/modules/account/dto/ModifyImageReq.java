package com.project.market.modules.account.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;

@Data
public class ModifyImageReq {

    @NotEmpty
    private String nickname;

    private MultipartFile profileImage;

}
