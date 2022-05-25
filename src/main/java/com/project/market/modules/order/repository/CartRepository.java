package com.project.market.modules.order.repository;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import java.util.List;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByItemAndAccount(Item item, Account account);

    Set<Cart> findCartsByAccount(Account account);

    @EntityGraph(attributePaths = {"item"}, type = EntityGraph.EntityGraphType.FETCH)
    Set<Cart> findCartsWithItemsByAccount(Account account);

    @EntityGraph(attributePaths = {"item"}, type = EntityGraph.EntityGraphType.FETCH)
    Cart findCartWithItemById(Long cartId);

    @Transactional
    void deleteCartsByAccount(Account account);


    Set<Cart> findByIdIn(List<Long> id);
    @EntityGraph(attributePaths = {"item"}, type = EntityGraph.EntityGraphType.FETCH)
    Set<Cart> findCartsWithItemByIdIn(List<Long> id);
}
