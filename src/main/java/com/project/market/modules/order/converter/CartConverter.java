package com.project.market.modules.order.converter;

import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.order.entity.Cart;
import com.project.market.modules.order.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartConverter {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    public Set<Cart> cartParameterConvert(String items) {
        String[] context = items.split(",");
        Set<Cart> cartSet = new HashSet<>();
        if (items.contains(":")) {
            // items = itemId:quantity,itemId:quantity 예) 110:2,120:1 (110번 상품 2개, 120번 상품 1개)
            for (String itemSet : context) {
                Long itemId = extractItemId(itemSet);
                int quantity = extractQuantity(itemSet);

                Item item = itemRepository.findById(itemId).orElseThrow();
                if (item.isDeleted()) {
                    log.info("삭제된 상품 주문 상품Id={}", item.getId());
                    throw new IllegalStateException("해당 상품은 존재하지 않습니다.");
                }
                cartSet.add(new Cart(item, quantity));
            }
        } else {
            // items = cartId,cartId,cartId 예) 110,123,22 (110번 Cart, 123번 Cart, 22번 Cart)
            List<Long> idList = new ArrayList<>();
            for (String cartIdStr : context) {
                Long cartId = Long.parseLong(cartIdStr);
                idList.add(cartId);
            }
            Set<Cart> findCarts = cartRepository.findByIdIn(idList);
            for (Cart findCart : findCarts) {
                Item item = findCart.getItem();
                int stock = item.getQuantity();
                if (stock < findCart.getQuantity()) {
                    log.info("수량 부족: 재고량={}, 주문량={}, 상품Id={}", stock, findCart.getQuantity(), item.getId());
                    throw new IllegalStateException("재고가 부족합니다.");
                }
                if (findCart.getItem().isDeleted()) {
                    log.info("삭제된 상품 주문 상품Id={}", item.getId());
                    throw new IllegalStateException("해당 상품은 존재하지 않습니다.");
                }
                cartSet.add(findCart);
            }
        }
        return cartSet;
    }

    private Long extractItemId(String itemSet) {
        return Long.parseLong(itemSet.split(":")[0]);
    }

    private int extractQuantity(String itemSet) {
        return Integer.parseInt(itemSet.split(":")[1]);
    }
}
