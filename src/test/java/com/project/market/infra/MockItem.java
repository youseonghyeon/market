package com.project.market.infra;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.service.ItemService;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.form.ItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@Component
public class MockItem {


    @Autowired
    private ItemService itemService;

    public static int PRICE = 6000;
    public static String ORIGIN_ADDRESS = "서울시 은평구";
    public static String DESC = "설명";
    public static boolean POST = true;
    public static boolean DIRECT = false;

    public Item createMockItem(Account account, String itemName) {
        ItemForm itemForm = createItemForm(itemName);
        return itemService.createNewItem(account, itemForm);
    }

    private ItemForm createItemForm(String itemName) {
        ItemForm itemForm = new ItemForm();
        itemForm.setName(itemName);
        itemForm.setPrice(PRICE);
        itemForm.setOriginAddress(ORIGIN_ADDRESS);
        itemForm.setDescription(DESC);
        itemForm.setPost(POST);
        itemForm.setDirect(DIRECT);
        return itemForm;
    }
}
