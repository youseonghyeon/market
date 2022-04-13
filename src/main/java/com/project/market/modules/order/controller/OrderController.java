package com.project.market.modules.order.controller;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.delivery.dao.DeliveryService;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.item.dao.repository.ItemRepository;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.form.PurchaseForm;
import com.project.market.modules.order.dao.OrderRepository;
import com.project.market.modules.order.dao.OrderService;
import com.project.market.modules.order.entity.Order;
import com.project.market.modules.order.form.OrderForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    private final OrderRepository orderRepository;
//    private final OrderFormValidator orderFormValidator;


    @GetMapping("/purchase")
    public String purchaseForm(@CurrentAccount Account account, @Valid PurchaseForm purchaseForm, Model model, RedirectAttributes attributes) {
        Item item = itemRepository.findItemReadOnlyById(purchaseForm.getItemId());
        // ===================================== 리팩토링
        if (item.getDeleted()) {
            throw new IllegalStateException("해당 상품은 존재하지 않습니다.");
        }
        if (item.isReserved()) {
            attributes.addFlashAttribute("errorMessage", "예약된 상품입니다.");
            return "redirect:/product/" + item.getId();
        }
        if (item.isMyItem(account)) {
            attributes.addFlashAttribute("errorMessage", "내 상품은 구매할 수 없습니다.");
            return "redirect:/product/" + item.getId();
        }
        // ===================================== 리팩토링
        model.addAttribute("orderForm", new OrderForm(item.getId(), purchaseForm.getMethod(), account));
        model.addAttribute("item", item);
        model.addAttribute("account", account);
        return "order/purchase";
    }


    @PostMapping("/purchase")
    public String purchase(@CurrentAccount Account account, @Valid OrderForm orderForm, Errors errors) {
        if (errors.hasErrors()) {
            return "order/purchase";
        }
        Item item = itemRepository.findById(orderForm.getItemId()).orElseThrow();
        if (!item.canPurchase(account)) {
            throw new IllegalStateException("구매할 수 없는 상품입니다.");
        }
        Order order = orderService.createOrder(account, orderForm, item);
        Delivery delivery = deliveryService.createDelivery(account, orderForm, item);
        orderService.join(order, delivery);

        if (orderForm.getPaymentMethod().equals("card")) {
            return "redirect:/purchase/card/" + order.getId();
        }
        if (orderForm.getPaymentMethod().equals("nobank")) {
            return "redirect:/purchase/pay/" + order.getId();
        }
        throw new IllegalStateException("결제 방식이 선택되지 않았습니다.");
    }

    @GetMapping("/purchase/pay/{orderId}")
    public String purchaseByNoBank(@CurrentAccount Account account, @PathVariable("orderId") Order order, Model model) throws IllegalAccessException {
        if (!order.isOwner(account)) {
            throw new IllegalAccessException("주문에 접근 권한이 없습니다.");
        }
        model.addAttribute("orderId", order.getId());
        return "order/pay/nobank";
    }

    @GetMapping("/purchase/card/{orderId}")
    public String purchaseByCard(@CurrentAccount Account account, @PathVariable("orderId") Order order, Model model) throws IllegalAccessException {
        if (!order.isOwner(account)) {
            throw new IllegalAccessException("주문에 접근 권한이 없습니다.");
        }
        model.addAttribute("orderId", order.getId());
        return "order/pay/card";
    }

    @PostMapping("/pay")
    public String payment(@RequestParam("order-id") Long orderId) {
        Order order = orderRepository.findOrderWithDeliveryById(orderId);
        Delivery delivery = order.getOrderDelivery();
        orderService.payment(order);
        deliveryService.startDelivery(delivery);

        return "redirect:/order/" + order.getId();
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
