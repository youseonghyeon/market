package com.project.market.modules.order.dto;

import lombok.Data;

@Data
public class AddCartDto {

    private Long itemId;
    private int quantity;
}
