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

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public Item createNewItem(Account account, ItemForm itemForm) {
        Item item = newItemBuild(account, itemForm);
        Set<String> tags = itemForm.getTags();
        if (!tags.isEmpty()) {
            joinItemWithTags(item, tags);
        }
        itemRepository.save(item);
        return item;
    }

    public void modifyItem(Item item, ItemForm itemForm) {
        item.editItem(itemForm);
    }

    private Item newItemBuild(Account account, ItemForm itemForm) {
        return Item.builder()
                .name(itemForm.getName())
                .price(itemForm.getPrice())
//                .coverPhoto(itemForm.getCoverPhoto())
                .coverPhoto("")
//                .photo(itemForm.getPhoto())
                .photo("")
                .originAddress(itemForm.getOriginAddress())
                .description(itemForm.getDescription())
                .enrolledDate(LocalDateTime.now())
                .enrolledBy(account)
                .shippingFee(DEFAULT_SHIPPING_FEE)
                .deleted(false)
                .expired(false)
                .tags(new ArrayList<>())
                .post(itemForm.isPost())
                .direct(itemForm.isDirect())
                .build();
    }

    public void joinItemWithTags(Item item, Set<String> tags) {
        Set<Tag> findTags = tagRepository.findAllByTitleIn(tags);
        for (Tag t : findTags) {
            item.getTags().add(t);
        }
    }

    public void deleteItem(Item item) {
        notificationRepository.deleteByItemId(item.getId());
        item.delete();
    }

    public void addFavorite(Account account, Item item) {
        Favorite favorite = new Favorite(account, item);
        favoriteRepository.save(favorite);
    }

    public void deleteFavorite(Favorite favorite) {
        favoriteRepository.delete(favorite);
    }

    public List<Item> findFavoriteItems(Account account) {
        return queryFactory.select(item)
                .from(favorite)
                .join(favorite.item, item)
                .where(favorite.account.id.eq(account.getId()))
                .fetch();
    }

}
