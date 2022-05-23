package com.project.market.modules.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseResDto {
    private String paymentMethod;

    private Long orderId;

}
