package com.project.market.modules.delivery.dao;

import com.project.market.WithAccount;
import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.dao.ItemRepository;
import com.project.market.modules.item.dao.ItemService;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.form.ItemForm;
import com.project.market.modules.order.form.OrderForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class DeliveryServiceTest {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    DeliveryService deliveryService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemService itemService;

    @Test
    @WithAccount("testUser")
    void createDelivery() {
//        Account account = accountRepository.findByLoginId("testUser");
//        ItemForm itemForm = createItemForm();
//        Long itemId = itemService.createNewItem(account, itemForm, new ArrayList<>());
//
//        OrderForm orderForm = new OrderForm();
//        orderForm.setDeliveryMethod();
//        orderForm.setDestinationAddress();
//        orderForm.
//        deliveryService.createDelivery(account);
        /**
         * OrderTest를 만들고 나서 테스팅
         * */
    }

    private ItemForm createItemForm() {
        ItemForm itemForm = new ItemForm();
        itemForm.setName("상품");
        itemForm.setPrice(1000);
        itemForm.setCoverPhoto("test.jpg");
        itemForm.setPhoto("test.jpg");
        itemForm.setOriginAddress("서울시 은평구");
        itemForm.setPost(true);
        itemForm.setDirect(false);
        return itemForm;
    }

    @Test
    void competeDelivery() {
    }
}
