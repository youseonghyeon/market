package com.project.market.modules.order.controller;

import com.project.market.infra.config.Config;
import com.project.market.infra.exception.UnAuthorizedException;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.order.dto.AddCartDto;
import com.project.market.modules.order.dto.QuantityModifyReq;
import com.project.market.modules.order.entity.Cart;
import com.project.market.modules.order.repository.CartRepository;
import com.project.market.modules.order.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final CartService cartService;

    @GetMapping("/cart")
    public String cartForm(@CurrentAccount Account account, Model model) {
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

    @GetMapping("/cart/add")
    @ResponseBody
    public String addCart(@CurrentAccount Account account, @ModelAttribute AddCartDto cartDto) {
        Item item = itemRepository.getById(cartDto.getItemId());
        Cart previousCart = cartRepository.findByItemAndAccount(item, account);
        if (previousCart != null) {
            return "상품이 이미 장바구니에 있습니다.";
        }
        int quantity = cartDto.getQuantity();
        if (item.getQuantity() < quantity) {
            return "재고가 부족합니다.";
        }
        Cart cart = new Cart(item, quantity, account);
        cartRepository.save(cart);
        return "장바구니에 저장되었습니다.";
    }

    @GetMapping("/cart/delete")
    @ResponseBody
    public String deleteCart(@CurrentAccount Account account, @RequestParam("cartId") Cart cart) {
        checkCartAccess(account, cart);
        cartRepository.delete(cart);
        return "ok";
    }

    @GetMapping("/cart/delete/all")
    @ResponseBody
    public String deleteAllOfCart(@CurrentAccount Account account) {
        cartRepository.deleteCartsByAccount(account);
        return "ok";
    }

    @PostMapping("/quantity/modify")
    @ResponseBody
    public String modifyQuantityInCart(@CurrentAccount Account account, @ModelAttribute QuantityModifyReq req) {
        Cart cart = cartRepository.findById(req.getCartId()).orElseThrow();
        checkCartAccess(account, cart);
        cartService.modifyQuantity(cart, req.getQuantity());
        return "ok";
    }

    private void checkCartAccess(Account account, Cart cart) {
        // 접근해야하는 카트가 본인의 카트인지 검증
        if (!cart.getAccount().equals(account)) {
            throw new UnAuthorizedException("접근 권한이 없습니다.");
        }
    }

}
