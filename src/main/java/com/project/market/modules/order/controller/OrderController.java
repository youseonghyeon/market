package com.project.market.modules.order.controller;

import com.project.market.infra.exception.UnAuthorizedException;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.delivery.service.DeliveryService;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.form.PurchaseForm;
import com.project.market.modules.order.repository.OrderRepository;
import com.project.market.modules.order.service.OrderService;
import com.project.market.modules.order.entity.Order;
import com.project.market.modules.order.form.OrderForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        if (item.isDeleted()) {
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
        orderService.mapping(order, delivery);

        if (orderForm.getPaymentMethod().equals("card")) {
            return "redirect:/purchase/card/" + order.getId();
        }
        if (orderForm.getPaymentMethod().equals("nobank")) {
            return "redirect:/purchase/pay/" + order.getId();
        }
        throw new IllegalStateException("결제 방식이 선택되지 않았습니다.");
    }

    @GetMapping("/purchase/pay/{orderId}")
    public String purchaseByNoBank(@CurrentAccount Account account, @PathVariable("orderId") Order order, Model model) {
        if (!order.isOwner(account)) {
            throw new UnAuthorizedException("주문에 접근 권한이 없습니다.");
        }
        model.addAttribute("orderId", order.getId());
        return "order/pay/nobank";
    }

    @GetMapping("/purchase/card/{orderId}")
    public String purchaseByCard(@CurrentAccount Account account, @PathVariable("orderId") Order order, Model model) {
        if (!order.isOwner(account)) {
            throw new UnAuthorizedException("주문에 접근 권한이 없습니다.");
        }
        model.addAttribute("orderId", order.getId());
        return "order/pay/card";
    }

    @PostMapping("/pay")
    public String payment(@RequestParam("order-id") Long orderId) {
        Order order = orderRepository.findOrderWithDeliveryById(orderId);
        Delivery delivery = order.getOrderDelivery();
        orderService.paymentComplete(order);
        deliveryService.departure(delivery);

        return "redirect:/order/" + order.getId();
    }

    @GetMapping("/order/{orderId}")
    public String orderDetailForm(@CurrentAccount Account account, @PathVariable("orderId") Order order, Model model) {
        if (!order.isOwner(account)) {
            throw new UnAuthorizedException("주문에 접근 권한이 없습니다.");
        }
        model.addAttribute("order", order);
        return "order/detail";
    }

    @GetMapping("/order/list")
    public String orderListForm(@CurrentAccount Account account, @RequestParam(name = "orderType", required = false) String orderType, Model model) {
        List<Order> orderList = orderService.findOrders(account, orderType);
        model.addAttribute("orderList", orderList);
        return "order/list";
    }

    @PostMapping("/order/cancel")
    public String orderCancel(@CurrentAccount Account account, @RequestParam("orderId") Order order) {
        if (!order.getCustomer().equals(account)) {
            throw new UnAuthorizedException("주문에 접근 권한이 없습니다.");
        }
        orderService.cancelOrder(order);
        return "redirect:/order/list";
    }

}
