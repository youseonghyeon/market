package com.project.market;

import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.dao.AccountService;
import com.project.market.modules.account.dao.ZoneRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.entity.Zone;
import com.project.market.modules.account.form.SignupForm;
import com.project.market.modules.item.dao.ItemRepository;
import com.project.market.modules.item.entity.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;

@EnableAsync
@SpringBootApplication
@RequiredArgsConstructor
public class App {

    private final ZoneRepository zoneRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final ItemRepository itemRepository;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }


    @PostConstruct
    public void init() {
        initZones();
        initUser();
        initItem();
    }

    public void initZones() {
        String[] cities = {"서울시", "경기도", "강원도", "충청남도", "충청북도", "전라남도", "전라북도", "경상북도", "경상남도"};
        for (String city : cities) {
            if (!zoneRepository.existsByCity(city)) {
                Zone zone = new Zone();
                zone.setCity(city);
                zoneRepository.save(zone);
            }
        }
    }

    public void initUser() {
        if (!accountRepository.existsByLoginId("admin")) {
            SignupForm signupForm = new SignupForm();
            signupForm.setLoginId("admin");
            signupForm.setPassword("qwerqwer");
            signupForm.setUsername("유성현");
            signupForm.setPhone("01012032022");
            signupForm.setEmail("mail@mail.com");
            accountService.saveNewAccount(signupForm);
            Account account = accountRepository.findByLoginId("admin");
            account.modifyRole("ROLE_ADMIN");
            accountRepository.save(account);
        }
    }

    private void initItem() {
        Account account = accountRepository.findByLoginId("admin");
        for (int i = 1; i <= 100; i++) {
            if (!itemRepository.existsByName("mockItem" + i)) {
                Item item = Item.builder()
                        .name("mockItem" + i)
                        .price(5000)
                        .coverPhoto("사진 없음")
                        .photo("사진 없음")
                        .originAddress("은평구 신사동")
                        .description("mockItem" + i + " 설명")
                        .enrolledDate(LocalDateTime.now())
                        .enrolledBy(account)
                        .shippingFee(2500)
                        .deleted(false)
                        .expired(false)
                        .direct(true)
                        .post(true)
                        .tags(new ArrayList<>())
                        .build();
                itemRepository.save(item);

            }
        }


    }
}
