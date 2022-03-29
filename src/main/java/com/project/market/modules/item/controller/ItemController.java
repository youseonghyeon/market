package com.project.market.modules.item.controller;

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
import com.project.market.modules.item.validator.ItemFormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ModelMapper modelMapper;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final ItemFormValidator itemFormValidator;

    @ExceptionHandler(IllegalAccessException.class)
    public String AccessEx() {
        log.warn("접근 권한 거부됨");
        return "exception/access";
    }

    @InitBinder("itemForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(itemFormValidator);
    }

    @GetMapping("/products/enroll")
    public String productEnrollForm(Model model) {
        List<Tag> whiteList = tagRepository.findTop100ByOrderByCountDesc();
        model.addAttribute("whiteList", whiteList);
        model.addAttribute("itemForm", new ItemForm());
        return "products/enroll";
    }

    @PostMapping("/products/enroll")
    public String productEnroll(@CurrentAccount Account account, @Valid ItemForm itemForm, @ModelAttribute TagForm tagForm, Errors errors) {
        if (errors.hasErrors()) {
            return "products/enroll";
        }
        tagService.createOrCountingTags(tagForm.getTags());
        // itemFormValidator 사용중
        Long itemId = itemService.createNewItem(account, itemForm, tagForm.getTags());
        return "redirect:/deal/" + itemId;
    }

    // 상품 페이지
    @GetMapping("/deal/{itemId}")
    public String productForm(@PathVariable("itemId") Item item, Model model) {
        model.addAttribute("item", item);
        return "products/product";
    }

    // 상품 리스트 조회
    @GetMapping("/products/list")
    public String productListForm(Pageable pageable,
                                  @RequestParam(value = "tag", required = false) String tag,
                                  @RequestParam(value = "order", required = false) String orderBy, Model model) {
        Page<Item> itemPage = itemRepository.findItemList(tag, orderBy, pageable);
        List<Tag> tagList = tagRepository.findTop20ByOrderByCountDesc();

        model.addAttribute("itemPage", itemPage);
        model.addAttribute("itemList", itemPage.getContent());
        model.addAttribute("tagList", tagList);
        return "products/list";
    }

    @GetMapping("/my-products/list")
    public String myProductList(@CurrentAccount Account account, Model model) {
        List<Item> itemList = itemRepository.findAllByEnrolledByOrderByEnrolledDateTimeDesc(account);
        model.addAttribute("itemList", itemList);
        return "products/my-list";
    }

    @GetMapping("/my-products/edit/{itemId}")
    public String editMyProduct(@CurrentAccount Account account, @PathVariable("itemId") Item item, Model model) throws IllegalAccessException {
        if (!item.isMyItem(account)) {
            throw new IllegalAccessException("접근 권한이 없습니다.");
        }
        model.addAttribute("itemForm", modelMapper.map(item, ItemForm.class));
        return "products/edit";
    }

    @PostMapping("/products/modify")
    public String modifyProduct(@CurrentAccount Account account, @ModelAttribute ItemForm itemForm, Errors errors) throws IllegalAccessException {
        if (errors.hasErrors()) {
            return "products/edit";
        }
        Item item = itemRepository.findById(itemForm.getId()).orElseThrow();
        if (!item.isMyItem(account)) {
            throw new IllegalAccessException("접근 권한이 없습니다.");
        }
        itemService.modifyItem(item, itemForm);
        return "redirect:/deal/" + itemForm.getId();
    }
}
