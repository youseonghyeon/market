package com.project.market;

import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.dao.AccountService;
import com.project.market.modules.account.dao.ZoneRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.entity.Zone;
import com.project.market.modules.account.form.SignupForm;
import com.project.market.modules.item.dao.ItemService;
import com.project.market.modules.item.dao.repository.ItemRepository;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.form.ItemForm;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@EnableAsync
@SpringBootApplication
@RequiredArgsConstructor
public class App {

    private final ZoneRepository zoneRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }


    @PostConstruct
    public void init() {
        initUser();
        initItem();
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
        for (int i = 1; i <= 100; i++) {
            if (!itemRepository.existsByName("mockItem" + i)) {
                ItemForm itemForm = new ItemForm();
                itemForm.setName("mockItem" + i);
                itemForm.setPrice(6000);
                itemForm.setQuantity(100);
                itemForm.setDescription("mockItem" + i + " 설명");
                itemForm.setShippingFee(2500);
                itemForm.getTags().add("test");
                itemForm.getTags().add("mock" + i);
                itemService.createNewItem(itemForm);
            }
        }


    }
}
