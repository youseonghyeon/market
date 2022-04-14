package com.project.market.modules.item.dao.repository;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.entity.Favorite;
import com.project.market.modules.item.entity.Item;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Favorite findByAccountAndItem(Account account, Item item);

    @EntityGraph(attributePaths = {"item"}, type = EntityGraph.EntityGraphType.FETCH)
    List<Favorite> findFavoritesByAccount(Account account);

    boolean existsByAccountAndItem(Account account, Item item);
}
