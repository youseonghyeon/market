package com.project.market.modules.account.entity;

import com.project.market.modules.account.form.AddressForm;
import com.project.market.modules.account.form.ProfileForm;
import com.project.market.modules.account.form.SignupForm;
import com.project.market.modules.account.util.PhoneUtils;
import com.project.market.modules.item.entity.Favorite;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.Tag;
import com.project.market.modules.notification.entity.Notification;
import com.project.market.modules.order.entity.Order;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.CascadeType.PERSIST;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(indexes = @Index(name = "i_account", columnList = "loginId"))

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

    private LocalDateTime joinedAt = LocalDateTime.now();

    private String bio;

    private String role;

    @Lob
    private String profileImage;

    private String passwordToken;

    private LocalDateTime passwordTokenCreatedAt;

    private String zoneCode;
    private String roadAddress;
    private String addressDetail;

    private int creditScore = 0;

    private boolean itemEnrollAlertByWeb = true;
    private boolean itemEnrollAlertByMail = false;

    private boolean deleted = false;

    @OneToMany(mappedBy = "enrolledBy")
    private List<Item> enrolledItem = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "recipient")
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    private Set<Favorite> favorites = new HashSet<>();

    @ManyToMany(cascade = PERSIST)
    private Set<Zone> zones = new HashSet<>();

    @ManyToMany(cascade = PERSIST)
    private Set<Tag> tags = new HashSet<>();

    public static Account createNewAccount(SignupForm signupForm) {
        Account account = new Account();
        account.username = signupForm.getUsername();
        account.loginId = signupForm.getLoginId();
        account.phone = PhoneUtils.trim(signupForm.getPhone());
        account.email = signupForm.getEmail();
        account.nickname = signupForm.getLoginId();
        // password??? Encoding?????? ????????? ???
        account.joinedAt = LocalDateTime.now();
        account.creditScore = 0;
        account.role = "ROLE_USER";
        account.itemEnrollAlertByWeb = true;
        account.itemEnrollAlertByMail = false;
        account.deleted = false;

        return account;
    }


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
            // ?????? ??????
            passwordTokenCreatedAt = LocalDateTime.now().minusDays(3);
        } else {
            // ?????? ??????
            passwordTokenCreatedAt = LocalDateTime.now();
        }
    }

    public void modifyAddress(AddressForm addressForm) {
        zoneCode = addressForm.getZoneCode();
        roadAddress = addressForm.getRoadAddress();
        addressDetail = addressForm.getAddressDetail();
    }

    public void withdrawal() {
        deleted = true;
    }
}
