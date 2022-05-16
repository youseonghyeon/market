package com.project.market.modules.item.entity;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.item.form.ItemForm;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;

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

    private String coverPhoto;

    private String photo;

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
        coverPhoto = null;
        photo = null;
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

    public void plusFavoriteCount() {
        favoriteCount++;
    }

    public void minusFavoriteCount() {
        favoriteCount--;
    }
}
