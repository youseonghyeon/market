package com.project.market.modules.item.dto;

import lombok.Data;

@Data
public class ModifyCommentReq {

    private Long commentId;

    private String content;
}
