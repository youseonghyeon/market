package com.project.market.modules.item.entity;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.dto.CommentDto;
import com.project.market.modules.superclass.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "item_id")
    private Item item;

    private String content;

    private Double star;

    private Integer depth;

    private boolean modified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "account_id")
    private Account author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "comment_id")
    private Comment parentComment;


    public static Comment createComment(Account author, Item item, CommentDto dto, Comment parentComment) {
        Comment comment = new Comment();
        comment.content = dto.getContent();
        comment.star = dto.getStar();
        comment.item = item;
        comment.depth = dto.getDepth();
        comment.author = author;
        if (parentComment != null) {
            comment.parentComment = parentComment;
        }
        return comment;
    }

    public void modify(String content) {
        this.content = content;
        modified = true;
    }
}
