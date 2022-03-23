package com.project.market.modules.account.entity;

import com.project.market.modules.account.form.ProfileForm;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.Tag;
import com.project.market.modules.order.entity.Order;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@NamedEntityGraph(name = "Account.withTags", attributeNodes = {
        @NamedAttributeNode("tags")})
@NamedEntityGraph(name = "Account.withEnrolledItems", attributeNodes = {
        @NamedAttributeNode("enrolledItem")})
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

    private String passwordConfirmToken;

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    @OneToMany(mappedBy = "enrolledBy")
    private List<Item> enrolledItem;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

    @ManyToMany
    private List<Tag> tags;


    public void modifyProfile(ProfileForm profileForm) {
        nickname = profileForm.getNickname();
        profileImage = profileForm.getProfileImage();
        email = profileForm.getEmail();
        phone = profileForm.getPhone();

    }

    public void modifyPassword(String newPassword) {
        password = newPassword;
    }

    public void modifyRole(String role) {
        this.role = role;
    }

    public void savePasswordToken(String token) {
        passwordConfirmToken = token;
    }

    public void expirePasswordToken() {
        passwordConfirmToken = null;
    }
}
