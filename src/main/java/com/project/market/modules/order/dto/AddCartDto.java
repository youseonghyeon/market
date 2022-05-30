package com.project.market.modules.order.dto;

import lombok.Data;

import java.util.Set;

@Data
public class AddCartDto {

    private Long itemId;
    private int quantity;

    private Set<Long> optionContentIds;
}
