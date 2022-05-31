package com.project.market.modules.item.form;

import com.project.market.modules.order.converter.CartConverter;
import com.project.market.modules.order.entity.Cart;
import lombok.Data;

import java.util.Set;

@Data
public class PurchaseForm {

    private String items;

    public Set<Cart> getCartList(CartConverter cartConverter) {
        return cartConverter.cartParameterConvert(this.items);
    }
}
