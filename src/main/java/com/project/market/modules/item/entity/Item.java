package com.project.market.modules.item.entity;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.order.entity.Orders;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

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

    @ManyToMany
    private List<Tag> tags;

}
