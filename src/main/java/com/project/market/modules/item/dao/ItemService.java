package com.project.market.modules.item.dao;

import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.dao.repository.FavoriteRepository;
import com.project.market.modules.item.dao.repository.ItemRepository;
import com.project.market.modules.item.dao.repository.TagRepository;
import com.project.market.modules.item.entity.*;
import com.project.market.modules.item.form.ItemForm;
import com.project.market.modules.notification.dao.NotificationRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.project.market.modules.item.entity.QFavorite.*;
import static com.project.market.modules.item.entity.QItem.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    public static Integer DEFAULT_SHIPPING_FEE = 2500;
    private final ItemRepository itemRepository;
    private final TagRepository tagRepository;
    private final JPAQueryFactory queryFactory;
    private final NotificationRepository notificationRepository;
    private final FavoriteRepository favoriteRepository;
    private final AccountRepository accountRepository;
    private final TagService tagService;

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

}
