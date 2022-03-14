package com.project.market.modules.item.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class ItemForm {

    @NotEmpty
    private String name;

    @NotBlank
    private int price;

    private String coverPhoto;

    private String photo;

    private String originAddress;

    // private List<Tag> tags;

}
