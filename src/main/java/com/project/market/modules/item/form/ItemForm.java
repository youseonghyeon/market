package com.project.market.modules.item.form;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @NotEmpty
    private String description;

    private String errorMessage;

    private Set<String> tags = new HashSet<>();

    private List<String> option1 = new ArrayList<>();
    private List<String> option2 = new ArrayList<>();
    private List<String> option3 = new ArrayList<>();
    private List<String> option4 = new ArrayList<>();
    private List<String> option5 = new ArrayList<>();

}
