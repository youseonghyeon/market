package com.project.market.modules.item.dao.repository;

import com.project.market.modules.item.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomItemRepository {

    Page<Item> findItemList(String search, String tagName, String orderBy, Pageable pageable);
}
