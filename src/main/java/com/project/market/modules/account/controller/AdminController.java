package com.project.market.modules.account.controller;

import com.project.market.infra.exception.UnAuthorizedException;
import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.account.service.AccountService;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.order.repository.OrderRepository;
import com.project.market.modules.order.entity.Order;
import com.project.market.modules.order.entity.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final OrderRepository orderRepository;

    @GetMapping("/manage")
    public String roleManagement(@CurrentAccount Account account, Model model) {
        List<Account> accounts = accountRepository.findAll();
        // TODO 페이징
        model.addAttribute("accountList", accounts);
        return "admin/management";
    }

    @GetMapping("/manage/{accountId}")
    public String editRoleForm(@PathVariable("accountId") Account account, Model model) {
        model.addAttribute(account);
        return "admin/edit-management";
    }

    @PostMapping("/manage/edit")
    public String editRole(@CurrentAccount Account account, @RequestParam("targetId") Long targetId,
                           @RequestParam("role") String role) {
        if (!account.getRole().equals("ROLE_ADMIN")) {
            throw new UnAuthorizedException();
        }
        Account targetAccount = accountRepository.findById(targetId).orElseThrow();
        accountService.modifyRole(targetAccount, role);
        return "redirect:/admin/manage/" + targetId;
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

    @GetMapping("/payment")
    public String paymentManageForm(Model model) {
        List<Order> orders = orderRepository.findOrdersByOrderStatusAndPaymentMethodOrderByOrderDateAsc(OrderStatus.WAITING, "nobank");
        model.addAttribute("orderList", orders);
        return "admin/payment-manage";
    }
}
