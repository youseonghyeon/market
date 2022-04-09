package com.project.market.modules.account.entity;

import com.project.market.modules.account.form.AddressForm;
import com.project.market.modules.account.form.ProfileForm;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.Tag;
import com.project.market.modules.notification.entity.Notification;
import com.project.market.modules.order.entity.Order;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.PERSIST;

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

    private LocalDateTime passwordTokenCreatedAt;

    private String zoneCode;
    private String roadAddress;
    private String addressDetail;

    private boolean itemEnrollAlertByWeb = true;
    private boolean itemEnrollAlertByMail = false;


    @OneToMany(mappedBy = "enrolledBy")
    private List<Item> enrolledItem = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    private List<Order> orders = new ArrayList<>();

    @ManyToMany(cascade = PERSIST)
    private List<Zone> zones = new ArrayList<>();

    @ManyToMany(cascade = PERSIST)
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "recipient")
    private List<Notification> notifications = new ArrayList<>();

    public void modifyProfile(ProfileForm profileForm) {
        nickname = profileForm.getNickname();
        profileImage = profileForm.getProfileImage();
        email = profileForm.getEmail();
        phone = profileForm.getPhone();
        itemEnrollAlertByWeb = profileForm.isItemEnrollAlertByWeb();
        itemEnrollAlertByMail = profileForm.isItemEnrollAlertByMail();
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
            passwordTokenCreatedAt = LocalDateTime.now().minusDays(3);
        } else {
            // 토큰 생성
            passwordTokenCreatedAt = LocalDateTime.now();
        }
    }

    public boolean isValidPasswordToken(String token) {
        return passwordToken != null &&
                passwordToken.equals(token) &&
                passwordTokenCreatedAt.isAfter(LocalDateTime.now().minusSeconds(600));
    }

    public void modifyAddress(AddressForm addressForm) {
        zoneCode = addressForm.getZoneCode();
        roadAddress = addressForm.getRoadAddress();
        addressDetail = addressForm.getAddressDetail();
    }

}
