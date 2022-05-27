package com.project.market.modules.item.service;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.dto.CommentDto;
import com.project.market.modules.item.dto.ModifyCommentReq;
import com.project.market.modules.item.entity.Comment;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.repository.CommentRepository;
import com.project.market.modules.item.repository.ItemRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final ItemRepository itemRepository;

    private final JPAQueryFactory queryFactory;

    public Comment createComment(Account account, Item item, CommentDto commentDto) {
        // validator를 추가해서 (상품을 구매한 사람에 한해서 하나의 댓글만 남길 수 있도록 함)

        // 부모 댓글(지금은 비활성화)
//        Long parentId = commentDto.getParentCommentId();
//        Comment parent = (parentId != null ? commentRepository.findById(parentId).orElseThrow() : null);

        // 댓글 생성/저장
        Comment comment = Comment.createComment(account, item, commentDto, null);
        commentRepository.save(comment);

        // 상품 별점 동기화
        return comment;
    }


    public Comment modifyComment(Comment comment, ModifyCommentReq commentReq) {
        comment.modify(commentReq.getContent());
        return comment;
    }

    public void deleteComment(Comment commentId) {
        commentRepository.delete(commentId);
    }
}
