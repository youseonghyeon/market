package com.project.market.modules.order.controller;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.dao.OrderRepository;
import com.project.market.modules.order.dao.OrderService;
import com.project.market.modules.order.entity.Orders;
import com.project.market.modules.order.form.OrderForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @ExceptionHandler(MissingPathVariableException.class)
    public String exceptionHandler(Model model) {
        model.addAttribute("error", "페이지에 접근할 수 없습니다.");
        return "error/missing-path-variable-exception";
    }

    @GetMapping("/purchase")
    public String purchaseForm(Model model, @RequestParam("itemId") Item item) {
        model.addAttribute(new OrderForm());
        model.addAttribute(item);
        return "order/order";
    }

    @PostMapping("/purchase")
    public String processPurchase(@CurrentAccount Account account, @Valid OrderForm orderForm,
                                  Errors errors, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            return "order/order";
        }
        Orders orders = orderService.processPurchase(account, orderForm);
        attributes.addFlashAttribute("message", "주문이 완료 되었습니다.");
        return "redirect:/order/" + orders.getId();
    }

    @GetMapping("/order/{orderId}")
    public String orderDetail(@CurrentAccount Account account, @PathVariable("orderId") Orders orders,
                              Model model) throws IllegalAccessException {
        if (!orders.checkOwnership(account)) {
            log.info("권한 x");
            throw new IllegalAccessException("주문에 접근 권한이 없습니다.");
        }
        model.addAttribute("order", orders);
        return "order/detail";
    }

    @GetMapping("/order/list")
    public String orderList(@CurrentAccount Account account,
                            @RequestParam(name = "orderType", required = false) String orderType,
                            Model model) {
        List<Orders> orderList;
        if (orderType != null) {
            orderList = orderService.findOrders(account, orderType);
        } else {
            orderList = orderService.findOrders(account);
        }
        model.addAttribute("orderList", orderList);
        return "order/list";
    }

}
