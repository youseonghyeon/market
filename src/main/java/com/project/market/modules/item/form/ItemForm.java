package com.project.market.modules.item.form;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.HashSet;
import java.util.Set;

@Data
public class ItemForm {

    private Long id;

    @NotEmpty
    private String name;

    @Positive
    private Integer price;

    private Integer quantity;

    private MultipartFile coverPhoto;

    private MultipartFile photo;

    private Integer shippingFee;

    @NotEmpty
    private String description;

    private String errorMessage;

    private Set<String> tags = new HashSet<>();

}
