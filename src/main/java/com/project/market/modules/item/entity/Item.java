package com.project.market.modules.item.entity;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.item.form.ItemForm;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@NamedEntityGraph(name = "Item.withTagAndEnrolledBy", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("enrolledBy")
})

public class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    private String description;

    private float rating;

    @ManyToOne(fetch = LAZY)
//    @JoinColumn(name = "account")
    private Account enrolledBy;

    private LocalDateTime enrolledDate;

    @OneToOne(fetch = LAZY)
    private Delivery delivery;

    private String coverPhoto;

    private String photo;

    private String originAddress;

    private boolean reserved;

    private boolean expired;

    private int shippingFee;

    private Boolean deleted;

    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<Tag> tags;

    // 배송/직거래
    private boolean post;
    private boolean direct;

    public void sold() {
        expired = true;
        reserved = true;
    }

    public boolean canPurchase(Account currentAccount) {
        return !(enrolledBy.equals(currentAccount) || deleted || expired);
    }

    public void editItem(ItemForm itemForm) {
        name = itemForm.getName();
        price = itemForm.getPrice();
        // TODO 해결해야 함
//        coverPhoto = itemForm.getCoverPhoto();
        coverPhoto = null;
//        photo = itemForm.getPhoto();
        photo = null;
        description = itemForm.getDescription();
        originAddress = itemForm.getOriginAddress();
        post = itemForm.isPost();
        direct = itemForm.isDirect();
    }

    public boolean isMyItem(Account account) {
        return enrolledBy.getId().equals(account.getId());
    }

    public boolean isAccessible() {
        return !deleted;
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

    public boolean deletable() {
        return !reserved && !deleted && !expired;
    }
}
