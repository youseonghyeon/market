package com.project.market.modules.item.controller;

import com.project.market.modules.account.dao.AccountService;
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
import com.project.market.modules.notification.dao.NotificationService;
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
import java.util.ArrayList;
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
    private final NotificationService notificationService;


    @ExceptionHandler(IllegalAccessException.class)
    public String AccessEx() {
        log.warn("접근 권한 거부됨");
        return "exception/access";
    }

    @InitBinder("itemForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(itemFormValidator);
    }

    @GetMapping("/product/enroll")
    public String productEnrollForm(Model model) {
        List<Tag> whiteList = tagRepository.findTop100ByOrderByCountDesc(); // 태그 목록 (최대 상위 100개)
        model.addAttribute("whiteList", whiteList);
        model.addAttribute("itemForm", new ItemForm());
        return "products/enroll";
    }

    @PostMapping("/product/enroll")
    public String productEnroll(@CurrentAccount Account account, @Valid ItemForm itemForm, @ModelAttribute TagForm tagForm, Errors errors) {
        if (errors.hasErrors()) {
            return "products/enroll";
        }
        tagForm.getTags().add("A");
        tagForm.getTags().add("B");
        tagForm.getTags().add("C");
        tagForm.getTags().add("D");

        tagService.createOrCountingTags(tagForm.getTags());
        Item item = itemService.createNewItem(account, itemForm, tagForm.getTags());

        /**
         * item에 있는 태그들을 관심태그로 등록한 회원에게 Web알림 전송
         */
        notificationService.noticeItemEnrollment(item);
        /**
         * item에 있는 태그들을 관심태그로 등록한 회원에게 메일 전송
         */
        notificationService.noticeByEmailItemEnrollment(item);
        return "redirect:/product/" + item.getId();
    }

    @GetMapping("/product/{itemId}")
    public String productForm(@PathVariable("itemId") Item item, Model model) {
        model.addAttribute("item", item);
        return "products/product";
    }

    @GetMapping("/product/list")
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

    @GetMapping("/product/my-list")
    public String myProductList(@CurrentAccount Account account, Model model) {
        List<Item> itemList = itemRepository.findAllByEnrolledByOrderByEnrolledDateTimeDesc(account);
        model.addAttribute("itemList", itemList);
        return "products/my-list";
    }

    @GetMapping("/product/edit/{itemId}")
    public String editMyProduct(@CurrentAccount Account account, @PathVariable("itemId") Item item, Model model) throws IllegalAccessException {
        if (!item.isMyItem(account)) {
            throw new IllegalAccessException("접근 권한이 없습니다.");
        }
        List<Tag> tagList = item.getTags();

        model.addAttribute("itemForm", modelMapper.map(item, ItemForm.class));
        model.addAttribute("tagList", tagList);
        return "products/edit";
    }

    @PostMapping("/product/modify")
    public String modifyProduct(@CurrentAccount Account account, @ModelAttribute ItemForm itemForm, Errors errors) throws IllegalAccessException {
        if (errors.hasErrors()) {
            return "products/edit";
        }
        Item item = itemRepository.findById(itemForm.getId()).orElseThrow();
        if (!item.isMyItem(account)) {
            throw new IllegalAccessException("접근 권한이 없습니다.");
        }
        itemService.modifyItem(item, itemForm);
        return "redirect:/product/" + itemForm.getId();
    }

    @PostMapping("/product/tag")
    public String addNewTag(@CurrentAccount Account account, @RequestParam("itemId") Item item, @RequestParam("tag") String tag) {
        List<String> tags = new ArrayList<>();
        tags.add(tag);
        tagService.createOrCountingTags(tags);
        itemService.joinItemWithTags(item, tags);
        return "redirect:/product/edit/" + item.getId();
    }
}
