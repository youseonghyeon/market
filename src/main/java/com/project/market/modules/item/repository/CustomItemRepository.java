package com.project.market.modules.item.repository;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.dto.ItemLookupDto;
import com.project.market.modules.item.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomItemRepository {

    Page<ItemLookupDto> findItemList(String search, String tagName, String orderBy, Pageable pageable);

    List<Item> findFavoriteItems(Account account);
}
