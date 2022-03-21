package com.project.market.modules.account.entity;

import com.project.market.modules.account.form.ProfileForm;
import com.project.market.modules.order.entity.Order;
import com.project.market.modules.item.entity.Item;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Account {

    @Id
    @GeneratedValue
    @Column(name = "account_id")
    private Long id;

    private String username;

    @Column(unique = true)
    private String loginId;

    @Column(unique = true)
    private String phone;

    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private LocalDateTime joinedAt;

    private String bio;

    private String role;

    @Lob
    private String profileImage;

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    @OneToMany(mappedBy = "enrolledBy")
    private List<Item> enrolledItem;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;


    public void modifyProfile(ProfileForm profileForm) {
        this.nickname = profileForm.getNickname();
        this.profileImage = profileForm.getProfileImage();
        this.email = profileForm.getEmail();
        this.phone = profileForm.getPhone();

    }

    public void modifyPassword(String newPassword) {
        this.password = newPassword;
    }
}
