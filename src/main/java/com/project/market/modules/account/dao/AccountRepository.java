package com.project.market.modules.account.dao;

import com.project.market.modules.account.entity.Account;
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

    @EntityGraph(value = "Account.withTags", type = EntityGraph.EntityGraphType.FETCH)
    Account findAccountWithTagById(Long id);
}
