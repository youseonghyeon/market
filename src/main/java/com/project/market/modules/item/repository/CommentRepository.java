package com.project.market.modules.item.repository;

import com.project.market.modules.item.entity.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = "author", type = EntityGraph.EntityGraphType.FETCH)
    List<Comment> findCommentsByItemIdOrderByCreatedDateDesc(Long itemId);

    Boolean existsByItemId(Long itemId);

}
