package com.project.market.modules.product.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class ProductForm {

    @NotEmpty
    private String name;

    @NotBlank
    private int price;

    @NotBlank
    private int quantity;

    private String coverPhoto;

    private String photo;

    // private List<Tag> tags;

}
