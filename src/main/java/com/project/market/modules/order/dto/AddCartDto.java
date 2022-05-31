package com.project.market.modules.order.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class AddCartDto {

    @NotNull
    private Long itemId;
    
    @NotNull
    private int quantity;

    private Set<Long> optionContentIds;
}
