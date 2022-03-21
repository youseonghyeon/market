package com.project.market.modules.item.dao;

import com.project.market.modules.item.entity.Item;

import java.util.List;

public interface CustomItemRepository {

    List<Item> findItemList(String tagName, String orderBy);
}
