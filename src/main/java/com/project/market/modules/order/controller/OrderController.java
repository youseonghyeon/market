package com.project.market.modules.order.controller;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.delivery.entity.DeliveryMethod;
import com.project.market.modules.item.dao.ItemRepository;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.order.dao.OrderService;
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


    @GetMapping("/purchase")
    public String purchaseForm(@CurrentAccount Account account,
                               @RequestParam("itemId") Item item,
                               @RequestParam("delivery") String deliveryMethod,
                               Model model,
                               RedirectAttributes attributes) {
        if (!item.canPurchase(account)) {
            attributes.addFlashAttribute("errorMassage", "구매할 수 없는 상품입니다.");
            return "redirect:/deal/" + item.getId();
        }
        OrderForm orderForm = new OrderForm(item.getId(), DeliveryMethod.valueOf(deliveryMethod));
        model.addAttribute("orderForm", orderForm);
        model.addAttribute("item", item);
        return "order/purchase";
    }

    @PostMapping("/purchase")
    public String purchase(@CurrentAccount Account account,
                                  @Valid OrderForm orderForm,
                                  Errors errors,
                                  RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            return "order/purchase";
        }
        Item item = itemRepository.findById(orderForm.getItemId()).orElseThrow();
        if (!item.canPurchase(account)) {
            throw new IllegalStateException("구매할 수 없는 상품입니다.");
        }
        Long orderId = orderService.processPurchase(account, orderForm, item);
        attributes.addFlashAttribute("message", "주문이 완료 되었습니다.");
        return "redirect:/order/" + orderId;
    }

    @GetMapping("/order/{orderId}")
    public String orderDetailForm(@CurrentAccount Account account,
                              @PathVariable("orderId") Order order,
                              Model model) throws IllegalAccessException {
        if (!order.isOwner(account)) {
            throw new IllegalAccessException("주문에 접근 권한이 없습니다.");
        }
        model.addAttribute("order", order);
        return "order/detail";
    }

    @GetMapping("/order/list")
    public String orderListForm(@CurrentAccount Account account,
                            @RequestParam(name = "orderType", required = false) String orderType,
                            Model model) {
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
