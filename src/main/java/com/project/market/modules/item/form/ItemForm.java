package com.project.market.modules.item.form;

import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Data
public class ItemForm {

    private Long id;

    @NotEmpty
    private String name;

    @Positive
    private Integer price;

    @NotEmpty
    private String coverPhoto;

    @NotEmpty
    private String photo;

    private String originAddress;

    private String description;

    private String post;
    private String direct;

    // private List<Tag> tags;

    public boolean getPostMethod() {
        return post != null && post.equals("true");
    }

    public boolean getDirectMethod() {
        return direct != null && direct.equals("true");
    }

}
