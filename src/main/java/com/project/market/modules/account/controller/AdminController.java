package com.project.market.modules.account.controller;

import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AccountRepository accountRepository;

    @GetMapping("/manage")
    public String roleManagement(@CurrentAccount Account account, Model model) {
        // TODO 인가 관리 .hasRole("admin" or "manager")
        List<Account> accounts = accountRepository.findAll();
        // TODO 페이징 적용
        model.addAttribute("accountList", accounts);
        return "admin/management";
    }

    @GetMapping("/manage/{accountId}")
    public String editRoleForm(@PathVariable("accountId") Account account, Model model) {
        model.addAttribute(account);
        return "admin/edit-management";
    }

    @GetMapping("/role")
    public String currierManagement(@RequestParam(value = "role", required = false) String role, Model model) {
        if (role == null) {
            role = "ROLE_USER";
        }
        List<Account> accountList = accountRepository.findByRoleIs(role);
        model.addAttribute(accountList);
        return null;
    }

    @GetMapping("/delivery/manage")
    public String deliveryManagement(Model model) {
        return "admin/delivery-manage";
    }

}
