package com.project.market.modules.item.dto;

import lombok.Data;

@Data
public class CommentDto {

    private Long itemId;

    private String content;

    private Integer depth;

    private Long authorId;

    private Double star;

    private Long parentCommentId;
}
