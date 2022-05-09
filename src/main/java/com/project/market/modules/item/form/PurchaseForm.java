package com.project.market.modules.item.form;

import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Data
public class PurchaseForm {

    @Positive
    private Long itemId;

}
