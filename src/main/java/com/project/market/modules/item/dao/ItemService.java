package com.project.market.modules.item.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.Tag;
import com.project.market.modules.item.form.ItemForm;
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
    public static Integer DEFAULT_SHIPPING_FEE = 2500;

    public Item createNewItem(Account account, ItemForm itemForm, List<String> tags) {

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
                .tags(new ArrayList<>())
                .build();

        if (!tags.isEmpty()) {
            List<Tag> findTags = tagRepository.findAllByTitleIn(tags);
            for (Tag t : findTags) {
                item.getTags().add(t);
            }
        }

        return itemRepository.save(item);
    }

    public void modifyItem(ItemForm itemForm) {
        Item item = itemRepository.findById(itemForm.getId()).orElseThrow();
        item.editItem(itemForm);
    }
}
