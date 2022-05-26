package com.project.market.modules.account.dto;

import lombok.Data;

@Data
public class ModifyImageRes {

    private String nickname;
    private String imagePath;

    public ModifyImageRes(String nickname, String imagePath) {
        this.nickname = nickname;
        this.imagePath = imagePath;
    }
}
