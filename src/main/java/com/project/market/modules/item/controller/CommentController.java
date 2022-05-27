package com.project.market.modules.item.controller;

import com.project.market.infra.exception.UnAuthorizedException;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.item.dto.CommentDto;
import com.project.market.modules.item.dto.CommentRes;
import com.project.market.modules.item.dto.ModifyCommentReq;
import com.project.market.modules.item.entity.Comment;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.repository.CommentRepository;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.item.service.CommentService;
import com.project.market.modules.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;

    private final CommentService commentService;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    // 등록
    @PostMapping("/comment/add")
    @ResponseBody
    public CommentRes createComment(@CurrentAccount Account account, @ModelAttribute CommentDto commentDto) {
        Item item = itemRepository.findById(commentDto.getItemId()).orElseThrow();
        Comment comment = commentService.createComment(account, item, commentDto);

        itemService.syncStar(item);
        // 일단은 답글 기능 없애고 리턴한 값을 최 상단에 배치
        return new CommentRes(comment.getContent(), comment.getCreatedDate(), comment.getAuthor().getNickname());
    }


    // 수정
    @PostMapping("/comment/modify")
    @ResponseBody
    public ModifyCommentReq modifyComment(@CurrentAccount Account account, @ModelAttribute ModifyCommentReq req) {
        Comment comment = commentRepository.findById(req.getCommentId()).orElseThrow();
        checkCommentAccess(account, comment);
        commentService.modifyComment(comment, req);
        return null;
    }

    // 삭제
    @PostMapping("/comment/delete")
    @ResponseBody
    public void deleteComment(@CurrentAccount Account account, @RequestParam("commentId") Comment comment) {
        checkCommentAccess(account, comment);
        commentService.deleteComment(comment);
    }

    private void checkCommentAccess(Account account, Comment comment) {
        if (!comment.getAuthor().equals(account)) {
            log.info("비정상 접근");
            throw new UnAuthorizedException("접근 권한 없음");
        }
    }

}
