package com.project.market;

import com.project.market.modules.account.dao.AccountService;
import com.project.market.modules.account.dao.ZoneRepository;
import com.project.market.modules.account.entity.Zone;
import com.project.market.modules.account.form.SignupForm;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
@RequiredArgsConstructor
public class App {

    private final ZoneRepository zoneRepository;
    private final AccountService accountService;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }


    @PostConstruct
    public void init() {
        initZones();
        initUser();
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
        SignupForm signupForm = new SignupForm();
        signupForm.setLoginId("user1");
        signupForm.setPassword("qwerqwer");
        signupForm.setUsername("유성현");
        signupForm.setPhone("01023778747");
        signupForm.setEmail("dolla_@naver.com");
        accountService.saveNewAccount(signupForm);
    }
}
