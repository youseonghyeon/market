package com.project.market.modules.order.dto;

import lombok.Data;

@Data
public class CurrentCartData {

    private int totalPrice;
    private int shippingFee;

    public CurrentCartData(int totalPrice, int shippingFee) {
        this.totalPrice = totalPrice;
        this.shippingFee = shippingFee;
    }
}
