package com.project.market.modules.account.entity;

import com.project.market.modules.account.form.ProfileForm;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.Tag;
import com.project.market.modules.order.entity.Order;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
@NamedEntityGraph(name = "Account.withZones", attributeNodes = {
        @NamedAttributeNode("zones")})
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

    private String passwordToken;

    private LocalDateTime passwordTokenCreateDate;

    @ManyToMany
    private List<Zone> zones = new ArrayList<>();

    @OneToMany(mappedBy = "enrolledBy")
    private List<Item> enrolledItem = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    private List<Order> orders = new ArrayList<>();

    @ManyToMany
    private List<Tag> tags = new ArrayList<>();


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
        passwordToken = token;
        if (token == null) {
            // 토큰 폐기
            passwordTokenCreateDate = LocalDateTime.now().minusDays(3);
        } else {
            // 토큰 생성
            passwordTokenCreateDate = LocalDateTime.now();
        }
    }

    public boolean isValidPasswordToken(String token) {
        return passwordToken != null &&
                passwordToken.equals(token) &&
                passwordTokenCreateDate.isAfter(LocalDateTime.now().minusSeconds(600));
    }


}
