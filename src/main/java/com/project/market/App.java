package com.project.market;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.form.SignupForm;
import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.account.service.AccountService;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.form.ItemForm;
import com.project.market.modules.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.util.UUID;

@EnableAsync
@SpringBootApplication
@RequiredArgsConstructor
@EnableJpaAuditing
public class App {

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final ItemRepository itemRepository;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }


    @PostConstruct
    public void init() {
        initUser();
//        initItem();
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
        long count = itemRepository.count();
        String itemName = UUID.randomUUID().toString().substring(0, 8);
        for (long i = count; i < 1000; i++) {
            ItemForm itemForm = new ItemForm();
            itemForm.setQuantity(100);
            itemForm.setPrice((int)(Math.random() * 1000));
            itemForm.setName(itemName);
            Item newItem = Item.createNewItem(itemForm);
            itemRepository.save(newItem);
        }
    }


}
