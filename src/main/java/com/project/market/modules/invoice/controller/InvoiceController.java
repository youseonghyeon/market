package com.project.market.modules.invoice.controller;

import com.project.market.infra.exception.UnAuthorizedException;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.superclass.BaseTimeEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class InvoiceController extends BaseTimeEntity {

    @PostMapping
    public String paymentConfirm(@CurrentAccount Account account, @RequestParam("invoiceId") String id) {
        adminCheck(account);

        return null;
    }

    private void adminCheck(Account account) {
        String role = account.getRole();
        if (!role.equals("admin")) {
            throw new UnAuthorizedException("접근 권한이 없습니다.");
        }
    }


}
