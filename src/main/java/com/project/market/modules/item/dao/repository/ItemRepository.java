package com.project.market.modules.item.dao.repository;

import com.project.market.modules.item.entity.Item;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemRepository extends JpaRepository<Item, Long>, CustomItemRepository {

    Item findByName(String itemName);

    List<Item> findAllByDeletedFalseOrderByEnrolledDateDesc();

    boolean existsByName(String name);

    Item findItemReadOnlyById(Long itemId);

    @EntityGraph(attributePaths = {"tags"}, type = EntityGraph.EntityGraphType.FETCH)
    Item findItemWithTagsById(Long itemId);

}
