package com.project.market.modules.order.controller;

import com.project.market.infra.exception.UnAuthorizedException;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.delivery.dao.DeliveryService;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.item.dao.repository.ItemRepository;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.form.PurchaseForm;
import com.project.market.modules.order.dao.OrderRepository;
import com.project.market.modules.order.dao.OrderService;
import com.project.market.modules.order.entity.CartItem;
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

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ItemRepository itemRepository;
    private final DeliveryService deliveryService;
    private final OrderRepository orderRepository;


    @GetMapping("/purchase")
    public String purchaseForm(@CurrentAccount Account account, @Valid Set<PurchaseForm> forms, Model model) {
//        Set<Long> itemIdSet = forms.stream().map(form -> form.getItemId()).collect(Collectors.toSet());

        // TODO 파라미터를 set으로 받아야 함 (API로 받는게 좋아보임)
        Set<CartItem> items = new HashSet<>();
        int totalPrice = 0;
        int deliveryFee = 2500;
        for (PurchaseForm form : forms) {
            Item item = itemRepository.findById(form.getItemId()).orElseThrow();
            if (item.isDeleted()) {
                throw new IllegalStateException("해당 상품은 존재하지 않습니다.");
            }
            CartItem cartItem = new CartItem(item, form.getQuantity());
            totalPrice += cartItem.getPrice();
            items.add(cartItem);
        }

        model.addAttribute("orderForm", new OrderForm(account));
        model.addAttribute("cartItems", items);
        model.addAttribute("deliveryFee", deliveryFee);
        model.addAttribute("totalPrice", totalPrice + deliveryFee);
        model.addAttribute("account", account);
        return "order/purchase";
    }


    @PostMapping("/purchase")
    public String purchase(@CurrentAccount Account account, @Valid OrderForm orderForm, Errors errors) {
        if (errors.hasErrors()) {
            return "order/purchase";
        }
        // 인자를 어떻게 받을지
        // item 가격 + 배송비 계산
        // 해서 Order 객체 생성
        // delivery 객체는 나중에 생성
//        Order order = orderService.createOrder(account, orderForm);
//        Delivery delivery = deliveryService.createDelivery(account, orderForm, item);
//        orderService.mapping(order, delivery);

//        if (orderForm.getPaymentMethod().equals("card")) {
//            return "redirect:/purchase/card/" + order.getId();
//        }
//        if (orderForm.getPaymentMethod().equals("nobank")) {
//            return "redirect:/purchase/pay/" + order.getId();
//        }
//        throw new IllegalStateException("결제 방식이 선택되지 않았습니다.");
        return null;
    }

    @GetMapping("/purchase/pay/{orderId}")
    public String purchaseByNoBank(@CurrentAccount Account account, @PathVariable("orderId") Order order, Model model) {
        myOrderValidator(account, order);
        model.addAttribute("orderId", order.getId());
        return "order/pay/nobank";
    }

    @GetMapping("/purchase/card/{orderId}")
    public String purchaseByCard(@CurrentAccount Account account, @PathVariable("orderId") Order order, Model model) {
        myOrderValidator(account, order);
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
        myOrderValidator(account, order);
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
        myOrderValidator(account, order);
        orderService.cancelOrder(order);
        return "redirect:/order/list";
    }

    private void myOrderValidator(Account account, Order order) {
        if (!order.isOwner(account) && !account.getRole().equals("ROLE_ADMIN")) {
            throw new UnAuthorizedException("주문에 접근 권한이 없습니다.");
        }
    }

}
