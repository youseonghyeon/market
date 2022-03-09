package com.project.market.modules.account.util;

import com.project.market.modules.account.entity.Account;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class AccountContext extends User {

    private Account account;

    public AccountContext(Account account, List<GrantedAuthority> roles) {
        super(account.getLoginId(), account.getPassword(), roles);
        this.account = account;
    }
}
