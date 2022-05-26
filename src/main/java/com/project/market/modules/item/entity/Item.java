package com.project.market.modules.item.entity;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.item.form.ItemForm;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;

@Slf4j
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@NamedEntityGraph(name = "Item.withTag", attributeNodes = {
        @NamedAttributeNode("tags")
})

public class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    private int quantity;

    private String description;

    private float rating = 0;

    private LocalDateTime enrolledDate = LocalDateTime.now();

    private String coverPhotoPath;

    private String photoPath;

    private boolean deleted = false;

    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<Tag> tags = new HashSet<>();

    private int favoriteCount = 0;

    public static Item createNewItem(ItemForm itemForm) {
        Item item = new Item();
        item.name = itemForm.getName();
        item.price = itemForm.getPrice();
        item.quantity = itemForm.getQuantity();
        item.description = itemForm.getDescription();
        item.enrolledDate = LocalDateTime.now();
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
        photoPath = "/" + itemForm.getId() + "/" + itemForm.getPhoto().getOriginalFilename();
        coverPhotoPath = "/" + itemForm.getId() + "/" + itemForm.getCoverPhoto().getOriginalFilename();
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getBetweenDate() {
        long betweenDay = ChronoUnit.DAYS.between(enrolledDate, LocalDateTime.now());
        if (betweenDay > 0) {
            return betweenDay + "일 전";
        }
        long betweenHour = ChronoUnit.HOURS.between(enrolledDate, LocalDateTime.now());
        if (betweenHour > 0) {
            return betweenHour + "시간 전";
        }
        long betweenMinute = ChronoUnit.MINUTES.between(enrolledDate, LocalDateTime.now());
        if (betweenMinute >= 5) {
            return betweenMinute + "분 전";
        }
        return "방금전";
    }

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

    public void uploadPhotos( ItemForm itemForm) {
        photoPath = "/" + 10 + "/" + itemForm.getPhoto().getOriginalFilename();
        coverPhotoPath = "/" + 10 + "/" + itemForm.getCoverPhoto().getOriginalFilename();
    }

    public void setPhotoPaths(String coverPhotoPath, String photoPath) {
        this.coverPhotoPath = coverPhotoPath;
        this.photoPath = photoPath;
    }
}
