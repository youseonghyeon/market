package com.project.market;

import com.project.market.modules.account.dao.ZoneRepository;
import com.project.market.modules.account.entity.Zone;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
@RequiredArgsConstructor
public class App {

    private final ZoneRepository zoneRepository;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }


    @PostConstruct
    public void init() {
        initZones();
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
}
