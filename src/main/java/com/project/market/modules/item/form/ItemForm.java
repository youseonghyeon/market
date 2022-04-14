package com.project.market.modules.item.form;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.Set;

@Data
public class ItemForm {

    private Long id;

    @NotEmpty
    private String name;

    @Positive
    private Integer price;

    private MultipartFile coverPhoto;

    private MultipartFile photo;

    private String originAddress;

    @NotEmpty
    private String description;

    private boolean post;
    private boolean direct;

    private String errorMessage;

    private Set<String> tags;

    // private List<Tag> tags;
//
//    public boolean getPostMethod() {
//        return post != null && post.equals("true");
//    }
//
//    public boolean getDirectMethod() {
//        return direct != null && direct.equals("true");
//    }

}
