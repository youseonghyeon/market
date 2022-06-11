package com.project.market.modules.item.form;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ItemForm {

    private Long id;

    @NotEmpty(message = "상품 이름을 입력해주세요.")
    private String name;

    @Positive(message = "1이상 21억 미만")
    @NotNull(message = "상품 가격을 입력해주세요.")
    private Integer price;

    @Positive(message = "1이상 21억 미만")
    @NotNull(message = "상품 수량을 입력해주세요.")
    private Integer quantity;

    @NotNull(message = "이미지를 필수로 등록해야합니다.")
    private MultipartFile coverPhoto;

    @NotNull(message = "이미지를 필수로 등록해야합니다.")
    private MultipartFile photo;

    private String description;

    private String errorMessage;

    private Set<String> tags = new HashSet<>();

    private List<String> option1 = new ArrayList<>();
    private List<String> option2 = new ArrayList<>();
    private List<String> option3 = new ArrayList<>();

}
