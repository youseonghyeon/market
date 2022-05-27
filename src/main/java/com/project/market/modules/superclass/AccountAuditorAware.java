package com.project.market.modules.superclass;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.security.AccountContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccountAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {

        AccountContext context = (AccountContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account account = context.getAccount();

        return Optional.ofNullable(account.getId());
    }
}
