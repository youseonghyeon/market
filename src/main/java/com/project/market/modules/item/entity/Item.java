package com.project.market.modules.item.entity;

import com.project.market.modules.item.entity.option.OptionTitle;
import com.project.market.modules.item.form.ItemForm;
import com.project.market.modules.superclass.BaseAccountEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@NamedEntityGraph(name = "Item.withTag", attributeNodes = {
        @NamedAttributeNode("tags")
})

public class Item extends BaseAccountEntity {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    private int quantity;

    private String description;

    private Double star = 0.0;
    private int ratingCount = 0;

    private String coverPhotoPath;

    private String photoPath;

    private boolean deleted = false;

    private int favoriteCount = 0;

    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "item")
    private List<OptionTitle> optionTitles = new ArrayList<>();

    @OneToMany(mappedBy = "item")
    private List<Comment> commentList = new ArrayList<>();

    public static Item createNewItem(ItemForm itemForm) {
        Item item = new Item();
        item.name = itemForm.getName();
        item.price = itemForm.getPrice();
        item.quantity = itemForm.getQuantity();
        item.description = itemForm.getDescription();
        item.deleted = false;
        return item;
    }

    public boolean isSoldOut() {
        return quantity == 0;
    }

    public boolean isPurchasable() {
        return !deleted && !isSoldOut();
    }

    public void editItem(ItemForm itemForm) {
        name = itemForm.getName();
        price = itemForm.getPrice();
        quantity = itemForm.getQuantity();
        description = itemForm.getDescription();
    }

    public boolean isDeleted() {
        return deleted;
    }

//    public String getBetweenDate() {
//        long betweenDay = ChronoUnit.DAYS.between(getCreatedDate(), LocalDateTime.now());
//        if (betweenDay > 0) {
//            return betweenDay + "일 전";
//        }
//        long betweenHour = ChronoUnit.HOURS.between(getCreatedDate(), LocalDateTime.now());
//        if (betweenHour > 0) {
//            return betweenHour + "시간 전";
//        }
//        long betweenMinute = ChronoUnit.MINUTES.between(getCreatedDate(), LocalDateTime.now());
//        if (betweenMinute >= 5) {
//            return betweenMinute + "분 전";
//        }
//        return "방금전";
//    }

    public void delete() {
        deleted = true;
    }

    public void plusQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void minusQuantity(int quantity) {
        this.quantity -= quantity;
        if (this.quantity < 0) {
            log.info("상품 수량 부족 itemId={}", id);
            throw new IllegalStateException("상품 수량 부족");
        }
    }

    public void plusFavoriteCount() {
        favoriteCount++;
    }

    public void minusFavoriteCount() {
        favoriteCount--;
    }

    public void uploadPhotos(ItemForm itemForm) {
        photoPath = "/" + 10 + "/" + itemForm.getPhoto().getOriginalFilename();
        coverPhotoPath = "/" + 10 + "/" + itemForm.getCoverPhoto().getOriginalFilename();
    }

    public void setPhotoPaths(String coverPhotoPath, String photoPath) {
        this.coverPhotoPath = coverPhotoPath;
        this.photoPath = photoPath;
    }


    public void syncStar(Double avgStar, Long totalCount) {
        this.star = avgStar;
        this.ratingCount = totalCount.intValue();
    }
}
