package com.project.market.modules.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ItemLookupDto {

    private Long id;

    private String name;

    private int price;

    private int quantity;

    private Double star;

    private int ratingCount;

    private String coverPhotoPath;

    private String photoPath;

    private int favoriteCount;

    private List<String> tagTitle;

    private Long commentCount;

    public ItemLookupDto(Long id, String name, int price, int quantity, Double star, int ratingCount, String coverPhotoPath, String photoPath, int favoriteCount, List<String> tagTitle, Long commentCount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.star = star;
        this.ratingCount = ratingCount;
        this.coverPhotoPath = coverPhotoPath;
        this.photoPath = photoPath;
        this.favoriteCount = favoriteCount;
        this.tagTitle = tagTitle;
        this.commentCount = commentCount;
    }
}
