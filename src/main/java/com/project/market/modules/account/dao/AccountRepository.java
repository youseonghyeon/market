package com.project.market.modules.account.dao;

import com.project.market.modules.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByLoginId(String loginId);

    boolean existsByPhone(String phone);


    Account findByLoginId(String loginId);
}
