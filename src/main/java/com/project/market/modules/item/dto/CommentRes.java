package com.project.market.modules.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentRes {

    private String content;

    private LocalDateTime commentDate;

    private String authorNickName;

    public CommentRes(String content, LocalDateTime commentDate, String authorNickName) {
        this.content = content;
        this.commentDate = commentDate;
        this.authorNickName = authorNickName;
    }
}
