package com.project.market.modules.item.entity;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.item.form.ItemForm;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
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
    @JoinColumn(name = "account")
    private Account enrolledBy;

    private LocalDateTime enrolledDateTime;

    @OneToOne(fetch = LAZY)
    private Delivery delivery;

    private String coverPhoto;

    private String photo;

    private String originAddress;

    private boolean reserved;

    private boolean expired;

    private int shippingFee;

    private Boolean deleted;

    @ManyToMany
    private List<Tag> tags;


    public void sold() {
        this.expired = true;
    }


    public boolean canPurchase(Account currentAccount) {
        if (expired) return false;
        if (enrolledBy.equals(currentAccount)) return false;
        if (deleted) return false;
        return true;
    }

    public void editItem(ItemForm itemForm) {
        this.name = itemForm.getName();
        this.price = itemForm.getPrice();
        this.coverPhoto = itemForm.getCoverPhoto();
        this.photo = itemForm.getPhoto();
        this.description = itemForm.getDescription();
        this.originAddress = itemForm.getOriginAddress();
    }

}
