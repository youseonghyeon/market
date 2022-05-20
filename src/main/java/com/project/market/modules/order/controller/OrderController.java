package com.project.market.modules.order.controller;

import com.project.market.infra.config.Config;
import com.project.market.infra.exception.UnAuthorizedException;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.delivery.service.DeliveryService;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.repository.CartRepository;
import com.project.market.modules.order.repository.OrderRepository;
import com.project.market.modules.order.service.OrderService;
import com.project.market.modules.order.entity.Cart;
import com.project.market.modules.order.entity.Order;
import com.project.market.modules.order.form.LastOrderForm;
import com.project.market.modules.order.form.OrderForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.internal.Errors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.ArrayList;
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
    private final CartRepository cartRepository;

    @GetMapping("/purchase")
    public String purchaseForm(@CurrentAccount Account account, @RequestParam("items") String param, Model model) {
        Set<Cart> items = new HashSet<>();
        int totalPrice = 0;
        int deliveryFee = Config.SHIPPING_FEE;
        if (param.contains(":")) {
            // /purchase?items=100:1,200:3  -> 100번 아이템 1개, 200번 아이템 3개
            itemQuantityConvertor(items, param);
        } else {
            // /purchase?items=102,103,188 -> cart엔티티의 id=102, id=103, id=188 아이템 3개
           cartIdsConvertor(items, param);
        }

        for (Cart item : items) {
            totalPrice += item.getPrice();
        }

        model.addAttribute("orderForm", new OrderForm(account));
        model.addAttribute("cartItems", items);
        model.addAttribute("deliveryFee", deliveryFee);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("account", account);
        model.addAttribute("items", param);
        return "order/purchase";
    }

    private void itemQuantityConvertor(Set<Cart> items, String param) {
        String[] itemSets = param.split(",");
        log.info("param={}", param);
        for (String itemSet : itemSets) {
            Long itemId = Long.parseLong(itemSet.split(":")[0]);
            int quantity = Integer.parseInt(itemSet.split(":")[1]);
            Item item = itemRepository.findById(itemId).orElseThrow();
            if (item.isDeleted()) {
                throw new IllegalStateException("해당 상품은 존재하지 않습니다.");
            }
            Cart cart = new Cart(item, quantity);
            items.add(cart);
        }
    }

    private void cartIdsConvertor(Set<Cart> items, String param) {
        String[] split = param.split(",");
        List<Long> idList = new ArrayList<>();
        for (String s : split) {
            Long id = Long.parseLong(s);
            idList.add(id);
        }
        Set<Cart> findCarts = cartRepository.findByIdIn(idList);
        for (Cart findCart : findCarts) {
            if (!findCart.getItem().isDeleted()) {
                items.add(findCart);
            }
        }
    }


    @PostMapping("/purchase")
    public String purchase(@CurrentAccount Account account, @Valid LastOrderForm orderForm, Errors errors) {
        if (errors.hasErrors()) {
            return "order/purchase";
        }
        System.out.println("orderForm = " + orderForm);
        // 여기서 가격 계산 후
        // 결제 시스템 on

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
        return "redirect:/";
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
