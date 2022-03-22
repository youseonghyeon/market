package com.project.market.modules.item.controller;

import com.project.market.modules.account.dao.AccountRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.item.dao.ItemRepository;
import com.project.market.modules.item.dao.ItemService;
import com.project.market.modules.item.dao.TagRepository;
import com.project.market.modules.item.dao.TagService;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.Tag;
import com.project.market.modules.item.form.ItemForm;
import com.project.market.modules.item.form.TagForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final TagService tagService;
    private final ModelMapper modelMapper;
    private final TagRepository tagRepository;
    private final AccountRepository accountRepository;

    @GetMapping("/products/enroll")
    public String productEnrollForm(Model model) {
        List<Tag> tagList = tagRepository.findTop100ByOrderByCountDesc();
        model.addAttribute("whiteList", tagList);
        model.addAttribute("itemForm", new ItemForm());
        return "products/enroll";
    }

    @PostMapping("/products/enroll")
    public String productEnroll(@CurrentAccount Account account,
                                @ModelAttribute @Valid ItemForm itemForm,
                                @ModelAttribute TagForm tagForm, Errors errors) {
        if (errors.hasErrors()) {
            return "products/enroll";
        }
        tagService.createOrCountingTags(tagForm.getTags());
        Item item = itemService.createNewItem(account, itemForm, tagForm.getTags());

        return "redirect:/deal/" + item.getId();
    }

    // 개별 상품 조회
    @GetMapping("/deal/{itemId}")
    public String productForm(@PathVariable("itemId") Item item, Model model) {
        model.addAttribute("item", item);
        return "products/product";
    }

    // 상품 리스트 조회
    @GetMapping("/products/list")
    public String productList(@RequestParam(value = "tag", required = false) String tag,
                              @RequestParam(value = "order", required = false) String orderBy,
                              Model model) {
        List<Item> itemList = itemRepository.findItemList(tag, orderBy);
        List<Tag> tagList = tagRepository.findTop20ByOrderByCountDesc();
        model.addAttribute("itemList", itemList);
        model.addAttribute("tagList", tagList);
        return "products/list";

    }

    @GetMapping("/my-products/list")
    public String myProductList(@CurrentAccount Account account, Model model) {
        List<Item> itemList = itemRepository.findAllByEnrolledByOrderByEnrolledDateTimeDesc(account);
        model.addAttribute(itemList);
        return "products/my-list";
    }

    @GetMapping("/my-products/edit/{itemId}")
    public String editMyProduct(@CurrentAccount Account account,
                                @PathVariable("itemId") Item item,
                                Model model) {
        if (!item.getEnrolledBy().getId().equals(account.getId())) {
            throw new IllegalStateException("본인이 등록한 상품이 아닙니다.");
        }
        model.addAttribute(modelMapper.map(item, ItemForm.class));
        return "products/edit";
    }

    @PostMapping("/products/modify")
    public String modifyProduct(@CurrentAccount Account account,
                                @ModelAttribute ItemForm itemForm,
                                Errors errors) throws IllegalAccessException {
        if (errors.hasErrors()) {
            return "products/edit";
        }
        Item item = itemRepository.findById(itemForm.getId()).orElseThrow();
        Account accountWithItem = accountRepository.findAccountWithEnrolledItemById(account.getId());
        if (!itemService.isMyItem(accountWithItem, item)) {
            throw new IllegalAccessException("접근 권한이 없습니다.");
        }
        itemService.modifyItem(item, itemForm);
        return "redirect:/deal/" + itemForm.getId();
    }
}
