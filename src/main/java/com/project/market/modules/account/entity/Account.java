package com.project.market.modules.account.entity;

import com.project.market.modules.product.entity.Product;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(unique = true)
    private String loginId;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private LocalDateTime joinedAt;

    private String bio;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Lob
    private String profileImage;

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    @OneToMany(mappedBy = "seller")
    private List<Product> enrolledProduct;

    
}
