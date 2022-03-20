package com.project.market.modules.item.controller;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.item.dao.ItemRepository;
import com.project.market.modules.item.dao.ItemService;
import com.project.market.modules.item.dao.TagService;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.form.ItemForm;
import com.project.market.modules.item.form.TagForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.security.InvalidParameterException;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final TagService tagService;
    private final ModelMapper modelMapper;

    @GetMapping("/products/enroll")
    public String productEnrollForm(Model model) {
        model.addAttribute(new ItemForm());
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
        if (item == null) {
            throw new InvalidParameterException("존재하지 않는 상품입니다.");
        }
        model.addAttribute(item);
        return "products/product";
    }

    // 상품 리스트 조회
    @GetMapping("/products/list")
    public String productList(Model model) {
        List<Item> itemList = itemRepository.findAllByExpiredIsFalse();
        model.addAttribute(itemList);
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
                                Errors errors) {
        if (errors.hasErrors()) {
            return "products/edit";
        }
        // 해당 상품이 내 상품인지 확인하는 validator 추가
        itemService.modifyItem(itemForm);
        return "redirect:/deal/" + itemForm.getId();
    }
}
