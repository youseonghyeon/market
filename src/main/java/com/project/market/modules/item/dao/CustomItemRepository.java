package com.project.market.modules.item.dao;

import com.project.market.modules.item.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomItemRepository {

    Page<Item> findItemList(String tagName, String orderBy, Pageable pageable);
}
