package com.project.market.modules.order.controller;

import com.project.market.infra.exception.UnAuthorizedException;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.order.converter.CartConverter;
import com.project.market.modules.order.dto.AddCartDto;
import com.project.market.modules.order.dto.CurrentCartData;
import com.project.market.modules.order.dto.QuantityModifyReq;
import com.project.market.modules.order.entity.Cart;
import com.project.market.modules.order.repository.CartRepository;
import com.project.market.modules.order.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${shipping.fee}")
    private int shippingFee;

    private final CartConverter cartConverter;

    @GetMapping("/cart")
    public String cartForm(@CurrentAccount Account account, Model model) {
        Set<Cart> cartList = cartRepository.findCartsByAccount(account);
        int totalPrice = 0;
        for (Cart cart : cartList) {
            totalPrice += cart.getPrice();
        }
        totalPrice += shippingFee;
        model.addAttribute("cartList", cartList);
        model.addAttribute("shippingFee", shippingFee);
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
    public int modifyQuantityInCart(@CurrentAccount Account account, @ModelAttribute QuantityModifyReq req) {
        Cart cart = cartRepository.findCartWithItemById(req.getCartId());
        checkCartAccess(account, cart);
        cartService.modifyQuantity(cart, req.getQuantity());

        return cart.getPrice();
    }

    private void checkCartAccess(Account account, Cart cart) {
        // 접근해야하는 카트가 본인의 카트인지 검증
        if (!cart.getAccount().equals(account)) {
            throw new UnAuthorizedException("접근 권한이 없습니다.");
        }
    }

    @GetMapping("/cart/data")
    @ResponseBody
    public CurrentCartData findCurrentData(@CurrentAccount Account account, @RequestParam(name = "cartIdList", required = false) String idList) {
        if (idList.trim().equals("")) {
            return new CurrentCartData(0, 0);
        }
        Set<Cart> cartList = cartConverter.cartParameterConvert(idList);
        int totalPrice = 0;
        int shippingPrice = 0;
        for (Cart cart : cartList) {
            checkCartAccess(account, cart);
            totalPrice += cart.getPrice();
        }
        if (totalPrice > 0) {
            totalPrice += shippingFee;
            shippingPrice = shippingFee;
        }

        return new CurrentCartData(totalPrice, shippingPrice);
    }

}
