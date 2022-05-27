package com.project.market.modules.item.service;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.entity.*;
import com.project.market.modules.item.form.ItemForm;
import com.project.market.modules.item.repository.CommentRepository;
import com.project.market.modules.item.repository.FavoriteRepository;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.item.repository.TagRepository;
import com.project.market.modules.notification.repository.NotificationRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.project.market.modules.item.entity.QComment.comment;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final TagRepository tagRepository;
    private final NotificationRepository notificationRepository;
    private final FavoriteRepository favoriteRepository;
    private final CommentRepository commentRepository;
    private final JPAQueryFactory queryFactory;

    public Item createNewItem(ItemForm itemForm) {
        Item item = Item.createNewItem(itemForm);
        editTags(item, itemForm);
        itemRepository.save(item);

        return item;
    }

    private void editTags(Item item, ItemForm itemForm) {
        Set<Tag> tagsInItem = item.getTags();
        tagsInItem.clear();

        Set<String> newTags = itemForm.getTags();
        if (!newTags.isEmpty()) {
            Set<Tag> findTags = tagRepository.findAllByTitleIn(newTags);
            tagsInItem.addAll(findTags);
        }
    }

    public void modifyItem(Item item, ItemForm itemForm) {
        item.editItem(itemForm);
        editTags(item, itemForm);

        itemRepository.save(item);
    }

    public void deleteItem(Item item) {
        notificationRepository.deleteByItemId(item.getId());
        item.delete();
    }

    public void addFavorite(Account account, Item item) {
        Favorite favorite = new Favorite(account, item);
        favoriteRepository.save(favorite);

        item.plusFavoriteCount();
    }

    public void deleteFavorite(Favorite favorite) {
        favoriteRepository.delete(favorite);

        Item item = favorite.getItem();
        item.minusFavoriteCount();
        itemRepository.save(item);
    }

    public void savePhotoPath(Item item, String coverPhotoPath, String photoPath) {
        item.setPhotoPaths(coverPhotoPath, photoPath);
    }

    public void syncStar(Item item) {
        // 댓글이 존재한다면
        if (commentRepository.existsByItemId(item.getId())) {
            Tuple tuple = queryFactory.select(comment.star.sum(), comment.star.count()).from(comment).fetchOne();
            Double totalStar = tuple.get(0, Double.class);
            Long totalCount = tuple.get(1, Long.class);
            Double avgStar = totalStar / totalCount;
            item.syncStar(avgStar, totalCount);
        }

    }

    @Getter
    @AllArgsConstructor
    static class StartTuple {
        Double sum;
        Long count;
    }
}
