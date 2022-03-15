package com.project.market.modules.item.dao;

import com.project.market.modules.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByExpiredIsFalse();
}
