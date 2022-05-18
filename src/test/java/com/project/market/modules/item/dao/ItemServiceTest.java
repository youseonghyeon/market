package com.project.market.modules.item.dao;

import com.project.market.WithAccount;
import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.item.repository.TagRepository;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.Tag;
import com.project.market.modules.item.form.ItemForm;
import com.project.market.modules.item.service.ItemService;
import com.project.market.modules.item.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ItemServiceTest {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemService itemService;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    TagService tagService;

    @Test
    @WithAccount("testUser")
    void createNewItem() {
        // given
        Account account = accountRepository.findByLoginId("testUser");
        ItemForm itemForm = createItemForm();
        Set<String> tagList = new HashSet<>();
        tagList.add("태그1");
        tagList.add("태그2");
        itemForm.getTags().add("태그1");
        itemForm.getTags().add("태그2");
        tagService.createOrCountingTag(tagList);
        //when
        itemService.createNewItem(itemForm);
        //then
        Item item = itemRepository.findByName("상품");
        assertEquals(item.getPrice(), 1000);
//        assertEquals(item.getCoverPhoto(), "test.jpg");
//        assertEquals(item.getPhoto(), "test.jpg");
        Set<Tag> tags = item.getTags();
        assertEquals(tags.size(), 2);
        Tag tag1 = tagRepository.findByTitle("태그1");
        Tag tag2 = tagRepository.findByTitle("태그2");
        assertTrue(tags.contains(tag1));
        assertTrue(tags.contains(tag2));
    }

    @Test
    @WithAccount("testUser")
    void modifyItem() {
        //given
        Account account = accountRepository.findByLoginId("testUser");
        ItemForm itemForm1 = createItemForm();
        Item item = itemService.createNewItem(itemForm1);
        //when
        ItemForm itemForm2 = new ItemForm();
        itemForm2.setName("상품2");
        itemForm2.setPrice(2000);
//        itemForm2.setCoverPhoto("없음");
//        itemForm2.setPhoto("없음");
        itemService.modifyItem(item, itemForm2);
        //then
        assertEquals(item.getName(), "상품2");
        assertEquals(item.getPrice(), 2000);
//        assertEquals(item.getCoverPhoto(), "없음");
//        assertEquals(item.getPhoto(), "없음");
    }

    private ItemForm createItemForm() {
        ItemForm itemForm = new ItemForm();
        itemForm.setName("상품");
        itemForm.setPrice(1000);
//        itemForm.setCoverPhoto("test.jpg");
//        itemForm.setPhoto("test.jpg");
        return itemForm;
    }
}
