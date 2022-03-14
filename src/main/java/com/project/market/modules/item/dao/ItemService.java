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

    public Item createNewItem(Account account, ItemForm itemForm) {
        int defaultShippingFee = 2500; // 사용자가 착불을 선택할 수 있게 변경해야 함

        Item item = Item.builder()
                .name(itemForm.getName())
                .price(itemForm.getPrice())
                .coverPhoto(itemForm.getCoverPhoto())
                .photo(itemForm.getPhoto())
                .enrolledDateTime(LocalDateTime.now())
                .originAddress("test-originAddress")
                .enrolledBy(account)
                .shippingFee(defaultShippingFee)
                .build();

        return itemRepository.save(item);
    }
}
