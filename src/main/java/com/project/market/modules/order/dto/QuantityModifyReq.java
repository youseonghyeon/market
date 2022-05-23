package com.project.market.modules.order.dto;

import lombok.Data;

@Data
public class QuantityModifyReq {

    private Long CartId;
    private int quantity;
}
