package com.project.market.modules.item.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.Tag;
import com.project.market.modules.item.form.ItemForm;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;
    private final JPAQueryFactory queryFactory;
    public static Integer DEFAULT_SHIPPING_FEE = 2500;

    public Long createNewItem(Account account, ItemForm itemForm, List<String> tags) {
        Item item = Item.builder()
                .name(itemForm.getName())
                .price(itemForm.getPrice())
                .coverPhoto(itemForm.getCoverPhoto())
                .photo(itemForm.getPhoto())
                .originAddress(itemForm.getOriginAddress())
                .description(itemForm.getDescription())
                .enrolledDateTime(LocalDateTime.now())
                .enrolledBy(account)
                .shippingFee(DEFAULT_SHIPPING_FEE)
                .deleted(false)
                .expired(false)
                .tags(new ArrayList<>())
                .post(itemForm.getPostMethod())
                .direct(itemForm.getDirectMethod())
                .build();

        if (!tags.isEmpty()) {
            List<Tag> findTags = tagRepository.findAllByTitleIn(tags);
            for (Tag t : findTags) {
                item.getTags().add(t);
            }
        }
        Item saveItem = itemRepository.save(item);

        return saveItem.getId();
    }

    public void modifyItem(Item item, ItemForm itemForm) {
        item.editItem(itemForm);
    }
}
