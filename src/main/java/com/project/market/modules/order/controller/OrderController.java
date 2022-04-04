package com.project.market.modules.order.controller;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.delivery.dao.DeliveryService;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.item.dao.ItemRepository;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.form.PurchaseForm;
import com.project.market.modules.order.dao.OrderService;
import com.project.market.modules.order.entity.Order;
import com.project.market.modules.order.form.OrderForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ItemRepository itemRepository;
    private final DeliveryService deliveryService;


    @GetMapping("/purchase")
    public String purchaseForm(@CurrentAccount Account account, @Valid PurchaseForm purchaseForm, Model model, RedirectAttributes attributes) {
        Item item = itemRepository.findItemReadOnlyById(purchaseForm.getItemId());
        if (!item.canPurchase(account)) {
            String errorMassage = "구매할 수 없는 상품입니다.";
            if (item.isMyItem(account)) {
                errorMassage = "내 상품은 구매할 수 없습니다.";
            }
            attributes.addFlashAttribute("errorMassage", errorMassage);
            return "redirect:/product/" + item.getId();
        }
        model.addAttribute("orderForm", new OrderForm(item.getId(), purchaseForm.getMethod()));
        model.addAttribute("item", item);
        return "order/purchase";
    }

    @PostMapping("/purchase")
    public String purchase(@CurrentAccount Account account, @Valid OrderForm orderForm, Errors errors,
                           RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            return "order/purchase";
        }
        Item item = itemRepository.findById(orderForm.getItemId()).orElseThrow();
        if (!item.canPurchase(account)) {
            throw new IllegalStateException("구매할 수 없는 상품입니다.");
        }
        Order order = orderService.createOrder(account, orderForm, item);
        // TODO 결제로직 추가
        // 1. 카드 결제 시 결제 페이지로 이동
        // 2. 무통장 결제 시 무동장 계좌 페이지로 이동
        Delivery delivery = deliveryService.createDelivery(account, orderForm, item);
        orderService.join(order, delivery);
        attributes.addFlashAttribute("message", "주문이 완료 되었습니다.");
        return "redirect:/order/" + order.getId();
    }

    @GetMapping("/purchase/pay")
    public String purchaseByNoBank() {
        return null;
    }

    @GetMapping("/purchase/card")
    public String purchaseByCard() {
        return null;
    }

    @GetMapping("/order/{orderId}")
    public String orderDetailForm(@CurrentAccount Account account, @PathVariable("orderId") Order order, Model model) throws IllegalAccessException {
        if (!order.isOwner(account)) {
            throw new IllegalAccessException("주문에 접근 권한이 없습니다.");
        }
        model.addAttribute("order", order);
        return "order/detail";
    }

    @GetMapping("/order/list")
    public String orderListForm(@CurrentAccount Account account, @RequestParam(name = "orderType", required = false) String orderType, Model model) {
        List<Order> orderList;
        if (orderType != null) {
            orderList = orderService.findOrders(account, orderType);
        } else {
            orderList = orderService.findOrders(account);
        }
        model.addAttribute("orderList", orderList);
        return "order/list";
    }

}
