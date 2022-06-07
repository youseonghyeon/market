package com.project.market.modules.order.converter;

import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.option.OptionContent;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.item.repository.OptionContentRepository;
import com.project.market.modules.order.entity.Cart;
import com.project.market.modules.order.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartConverter {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final OptionContentRepository optionContentRepository;

    public Set<Cart> cartParameterConvert(String items) {
        String[] context = items.split(",");
        Set<Cart> cartSet = new HashSet<>();

        if (isParameterMethod(items)) {
            for (String itemSet : context) {
                Item item = itemRepository.findById(extractItemId(itemSet)).orElseThrow();
                int quantity = extractQuantity(itemSet);
                List<Long> optionContentIds = extractOptionIds(itemSet);
                List<OptionContent> optionContents = optionContentRepository.findAllById(optionContentIds);
                StringBuilder sb = new StringBuilder();
                for (OptionContent content : optionContents) {
                    sb.append(content.getContent()).append(" ");
                }

                checkDeleteValidator(item);
                cartSet.add(new Cart(item, quantity, sb.toString().trim()));
            }
        } else if (isCartMethod(items)) {
            List<Long> cartIds = Arrays.stream(context).map(Long::parseLong).collect(Collectors.toList());
            Set<Cart> CartList = cartRepository.findByIdIn(cartIds);

            for (Cart cart : CartList) {
                Item item = cart.getItem();

                stockValidator(cart, item);
                checkDeleteValidator(item);
                cartSet.add(cart);
            }
        }
        return cartSet;
    }

    private List<Long> extractOptionIds(String itemSet) {
        String[] split = itemSet.split("~");
        List<Long> ids = new ArrayList<>();
        for (int i = 1; i < split.length; i++) {
            ids.add(Long.parseLong(split[i]));
        }
        return ids;
    }

    private void checkDeleteValidator(Item item) {
        if (item.isDeleted()) {
            log.info("삭제된 상품 주문 상품Id={}", item.getId());
            throw new IllegalStateException("해당 상품은 존재하지 않습니다.");
        }
    }

    private void stockValidator(Cart cart, Item item) {
        if (item.getQuantity() < cart.getQuantity()) {
            log.info("수량 부족: 재고량={}, 주문량={}, 상품Id={}", item.getQuantity(), cart.getQuantity(), item.getId());
            throw new IllegalStateException("재고가 부족합니다.");
        }
    }

    private boolean isParameterMethod(String items) {
        // items = itemId:quantity,itemId:quantity 예) 110:2,120:1 (110번 상품 2개, 120번 상품 1개)
        return items.contains(":");
    }

    private boolean isCartMethod(String items) {
        // items = cartId,cartId,cartId 예) 110,123,22 (110번 Cart, 123번 Cart, 22번 Cart)
        return !items.contains(":");
    }

    private Long extractItemId(String itemSet) {
        return Long.parseLong(itemSet.split(":")[0]);
    }

    private int extractQuantity(String itemSet) {
        String str = itemSet.split(":")[1];
        String value = str.split("~")[0];

        return Integer.parseInt(value);
    }
}
