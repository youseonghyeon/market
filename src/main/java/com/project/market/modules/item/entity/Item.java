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

    // 배송/직거래
    private boolean post;
    private boolean direct;

    public void sold() {
        this.expired = true;
    }

    public boolean canPurchase(Account currentAccount) {
        return !(enrolledBy.equals(currentAccount) || deleted || expired);
    }

    public void editItem(ItemForm itemForm) {
        name = itemForm.getName();
        price = itemForm.getPrice();
        coverPhoto = itemForm.getCoverPhoto();
        photo = itemForm.getPhoto();
        description = itemForm.getDescription();
        originAddress = itemForm.getOriginAddress();
        post = itemForm.getPostMethod();
        direct = itemForm.getDirectMethod();
    }

    public boolean isMyItem(Account account) {
        return enrolledBy.getId().equals(account.getId());
    }

}
