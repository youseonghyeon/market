package com.project.market.modules.account.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.entity.Tag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByLoginId(String loginId);

    boolean existsByPhone(String phone);

    List<Account> findByRoleIs(String role);

    Account findByLoginId(String loginId);

    @EntityGraph(attributePaths = {"tags"}, type = EntityGraph.EntityGraphType.FETCH)
    Account findAccountWithTagsById(Long id);

    @EntityGraph(value = "Account.withZones", type = EntityGraph.EntityGraphType.FETCH)
    Account findAccountWithZonesById(Long id);

    @EntityGraph(value = "Account.withEnrolledItems", type = EntityGraph.EntityGraphType.FETCH)
    Account findAccountWithEnrolledItemById(Long id);

    Account findByPasswordToken(String token);

    Account findByEmail(String email);

    @EntityGraph(attributePaths = {"notifications"}, type = EntityGraph.EntityGraphType.FETCH)
    Account findAccountWithNotificationById(Long id);

    Account findWithFavoritesById(Long id);
}
