package com.project.market.modules.order.controller;

import com.project.market.infra.exception.UnAuthorizedException;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.delivery.service.DeliveryService;
import com.project.market.modules.item.form.PurchaseForm;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.order.converter.CartConverter;
import com.project.market.modules.order.dto.PurchaseRes;
import com.project.market.modules.order.entity.Cart;
import com.project.market.modules.order.entity.Order;
import com.project.market.modules.order.form.LastOrderForm;
import com.project.market.modules.order.form.OrderForm;
import com.project.market.modules.order.repository.CartRepository;
import com.project.market.modules.order.repository.OrderRepository;
import com.project.market.modules.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final CartRepository cartRepository;
    private final DeliveryService deliveryService;
    private final CartConverter cartConverter;

    @Value("${credit.account.number}")
    private String accountNumber;
    @Value("${credit.company}")
    private String creditCompany;

    @Value("${shipping.fee}")
    private int shippingFee;

    @GetMapping("/purchase")
    public String purchaseForm(@CurrentAccount Account account, PurchaseForm purchaseForm, Model model) {
        Set<Cart> cartList = purchaseForm.getCartList(cartConverter);
        int totalPrice = 0;

        for (Cart cart : cartList) {
            totalPrice += cart.getPrice();
        }
        totalPrice += shippingFee;

        model.addAttribute("orderForm", new OrderForm(account));
        model.addAttribute("cartItems", cartList);
        model.addAttribute("deliveryFee", shippingFee);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("account", account);
        model.addAttribute("items", purchaseForm.getItems());
        return "order/purchase";
    }


    @PostMapping("/purchase")
    @ResponseBody
    public PurchaseRes purchase(@CurrentAccount Account account, @Valid LastOrderForm orderForm) {
        Set<Cart> cartList = cartConverter.cartParameterConvert(orderForm.getItems());
        Order order = orderService.createOrder(account, orderForm, cartList);

        // TODO 전자 결제 모듈 추가
        return new PurchaseRes(orderForm.getPaymentMethod(), order.getId());

    }

    @GetMapping("/nobank/{orderId}")
    public String nobankDesc(@CurrentAccount Account account, @PathVariable("orderId") Long orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        if (!order.getPaymentMethod().equals("nobank")) {
            throw new IllegalStateException("무통장 결제 주문이 아닙니다.");
        }
        if (!order.getCustomer().equals(account)) {
            throw new UnAuthorizedException("접근 권한이 없습니다.");
        }
        model.addAttribute("order", order);
        model.addAttribute("accountNumber", accountNumber);
        model.addAttribute("creditCompany", creditCompany);
        return "order/pay/nobank";
    }

//    @GetMapping("/purchase/pay/{orderId}")
//    public String purchaseByNoBank(@CurrentAccount Account account, @PathVariable("orderId") Order order, Model model) {
//        myOrderValidator(account, order);
//        model.addAttribute("orderId", order.getId());
//        return "order/pay/nobank";
//    }

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
    public String orderListForm(Pageable pageable, @CurrentAccount Account account, @RequestParam(name = "orderType", required = false) String orderType, Model model) {
        List<Order> orderList = orderRepository.findOrdersWithCartAndItemAndDeliveryByAccount(account, pageable);
        model.addAttribute("orderList", orderList);
        return "order/history";
    }

    @PostMapping("/order/cancel")
    public String orderCancel(@CurrentAccount Account account, @RequestParam("orderId") Long orderId) {
        Order order = orderRepository.findWithCartsById(orderId);
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
