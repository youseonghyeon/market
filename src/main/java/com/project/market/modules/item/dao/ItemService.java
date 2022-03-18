package com.project.market.modules.item.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.form.ItemForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    public static Integer DEFAULT_SHIPPING_FEE = 2500;

    public Item createNewItem(Account account, ItemForm itemForm) {

        Item item = Item.builder()
                .name(itemForm.getName())
                .price(itemForm.getPrice())
                .coverPhoto(itemForm.getCoverPhoto())
                .photo(itemForm.getPhoto())
                .originAddress(itemForm.getOriginAddress())
                .enrolledDateTime(LocalDateTime.now())
                .enrolledBy(account)
                .shippingFee(DEFAULT_SHIPPING_FEE)
                .build();

        return itemRepository.save(item);
    }
}
