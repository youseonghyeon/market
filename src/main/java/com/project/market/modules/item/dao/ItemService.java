package com.project.market.modules.item.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.Tag;
import com.project.market.modules.item.form.ItemForm;
import com.project.market.modules.notification.dao.NotificationRepository;
import com.project.market.modules.notification.dao.NotificationService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    public static Integer DEFAULT_SHIPPING_FEE = 2500;
    private final ItemRepository itemRepository;
    private final TagRepository tagRepository;
    private final JPAQueryFactory queryFactory;
    private final NotificationRepository notificationRepository;

    public Item createNewItem(Account account, ItemForm itemForm, Set<String> tags) {
        Item item = newItemBuild(account, itemForm);
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
                .coverPhoto(itemForm.getCoverPhoto())
                .photo(itemForm.getPhoto())
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
}
