package com.project.market.modules.order.controller;

import com.project.market.infra.config.Config;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.order.dto.AddCartDto;
import com.project.market.modules.order.entity.Cart;
import com.project.market.modules.order.repository.CartRepository;
import com.project.market.modules.order.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final CartService cartService;

    @GetMapping("/cart/add")
    @ResponseBody
    public String addCart(@CurrentAccount Account account, @ModelAttribute AddCartDto cartDto) {
        Item item = itemRepository.getById(cartDto.getItemId());
        Cart previousCart = cartRepository.findByItemAndAccount(item, account);
        if (previousCart != null) {
            return "상품이 이미 장바구니에 있습니다.";
        }
        int quantity = cartDto.getQuantity();
        Cart cart = new Cart(item, quantity, account);
        cartRepository.save(cart);
        return "장바구니에 저장되었습니다.";
    }

    @GetMapping("/cart/delete")
    @ResponseBody
    public Integer deleteCart(@CurrentAccount Account account, @RequestParam("cartId") Long cartId) {
        cartRepository.deleteById(cartId);
        Set<Cart> carts = cartRepository.findCartsByAccount(account);
        Integer totalPrice = 0;
        for (Cart cart : carts) {
            totalPrice += cart.getPrice();
        }

        return totalPrice;
    }

    @GetMapping("/cart/delete/all")
    @ResponseBody
    public String deleteAllOfCart(@CurrentAccount Account account) {
//        cartService.deleteCartsByAccount(account);
        cartRepository.deleteCartsByAccount(account);
        log.info("삭제");
        return "ok";
    }

    @GetMapping("/cart")
    public String myCart(@CurrentAccount Account account, Model model) {
        Set<Cart> cartList = cartRepository.findCartsByAccount(account);
        int totalPrice = 0;
        for (Cart cart : cartList) {
            totalPrice += cart.getPrice();
        }
        totalPrice += Config.SHIPPING_FEE;
        model.addAttribute("cartList", cartList);
        model.addAttribute("shippingFee", Config.SHIPPING_FEE);
        model.addAttribute("totalPrice", totalPrice);
        return "products/cart";

    }


}
