package com.project.market.modules.security;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.security.AccountContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccountAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }

        AccountContext context = (AccountContext) auth.getPrincipal();
        Account account = context.getAccount();

        return Optional.ofNullable(account.getId());
    }
}
