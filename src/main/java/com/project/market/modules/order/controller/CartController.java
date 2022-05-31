package com.project.market.modules.order.controller;

import com.project.market.infra.exception.UnAuthorizedException;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.option.OptionContent;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.item.repository.OptionContentRepository;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartRepository cartRepository;
    private final CartService cartService;
    private final ItemRepository itemRepository;
    private final OptionContentRepository optionContentRepository;
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

    @PostMapping("/cart")
    @ResponseBody
    public String addCart(@CurrentAccount Account account, @ModelAttribute AddCartDto cartDto) {
        Item item = itemRepository.getById(cartDto.getItemId());
        // 옵션 내용
        List<OptionContent> optionContents = optionContentRepository.findAllById(cartDto.getOptionContentIds());
        // 옵션 내용을 저장하기위해 String 으로 변환(띄어쓰기로 분리) -> "검은색 플라스틱 가죽"
        String contents = getTitlesToString(optionContents);

        // 동일한 상품 + 동일한 옵션이 카트에 있는지 확인
        if (cartRepository.existsByItemAndAccountAndOptions(item, account, contents)) {
            return "상품이 이미 장바구니에 있습니다.";
        }

        int quantity = cartDto.getQuantity();
        if (item.getQuantity() < quantity) {
            return "재고가 부족합니다.";
        }

        cartRepository.save(new Cart(item, quantity, account, contents));
        return "장바구니에 저장되었습니다.";
    }

    private String getTitlesToString(List<OptionContent> optionContents) {
        StringBuilder sb = new StringBuilder();
        for (OptionContent optionContent : optionContents) {
            if (optionContent != null) {
                sb.append(optionContent.getContent()).append(" ");
            }
        }
        return sb.toString().trim();
    }

    @DeleteMapping("/cart")
    @ResponseBody
    public String deleteCart(@CurrentAccount Account account, @RequestParam("cartId") Cart cart) {
        verifyCartAccess(account, cart);
        cartRepository.delete(cart);
        return "ok";
    }

    @DeleteMapping("/cart/all")
    @ResponseBody
    public String deleteAllOfCart(@CurrentAccount Account account) {
        cartRepository.deleteCartsByAccount(account);
        return "ok";
    }

    @PostMapping("/cart/quantity")
    @ResponseBody
    public int modifyQuantityInCart(@CurrentAccount Account account, @ModelAttribute QuantityModifyReq req) {
        Cart cart = cartRepository.findCartWithItemById(req.getCartId());
        verifyCartAccess(account, cart);
        cartService.modifyQuantity(cart, req.getQuantity());

        return cart.getPrice();
    }

    @GetMapping("/cart/refetch")
    @ResponseBody
    public CurrentCartData findCurrentData(
            @CurrentAccount Account account,
            @RequestParam(name = "cartIdList", required = false) String idList) {
        if (!StringUtils.hasText(idList)) {
            return new CurrentCartData(0, 0);
        }
        Set<Cart> cartList = cartConverter.cartParameterConvert(idList);

        int total = 0;
        int shippingPrice = 0;
        for (Cart cart : cartList) {
            verifyCartAccess(account, cart);
            total += cart.getPrice();
        }

        // 합계금액이 0원이 아니면 배송비 추가
        if (total != 0) {
            total += shippingFee;
            shippingPrice = shippingFee;
        }

        return new CurrentCartData(total, shippingPrice);
    }

    private void verifyCartAccess(Account account, Cart cart) {
        // 접근해야하는 카트가 본인의 카트인지 검증
        if (!cart.getAccount().equals(account)) {
            throw new UnAuthorizedException("접근 권한이 없습니다.");
        }
    }

}
