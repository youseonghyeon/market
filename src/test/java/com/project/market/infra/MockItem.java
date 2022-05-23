package com.project.market.infra;

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
    public static int SHIPPING_PRICE = 2500;
    public static String DESC = "설명";
    public static int QUANTITY = 100;

    public Item createMockItem(String itemName) {
        ItemForm itemForm = createItemForm(itemName);
        return itemService.createNewItem(itemForm);
    }

    public ItemForm createItemForm(String itemName) {
        ItemForm itemForm = new ItemForm();
        itemForm.setName(itemName);
        itemForm.setPrice(PRICE);
        itemForm.setQuantity(QUANTITY);
        itemForm.setDescription(DESC);
        return itemForm;
    }
}
