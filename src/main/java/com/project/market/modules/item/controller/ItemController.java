package com.project.market.modules.item.controller;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.item.dao.repository.ItemRepository;
import com.project.market.modules.item.dao.ItemService;
import com.project.market.modules.item.dao.repository.TagRepository;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public String productEnroll(@CurrentAccount Account account, @Valid ItemForm itemForm, Errors errors) {
        if (errors.hasErrors()) {
            return "products/enroll";
        }
        // TODO 태그 추가 기능을 넣어야 함
        TagForm tagForm = new TagForm();
        tagForm.getTags().add("A");
        tagForm.getTags().add("B");
        tagForm.getTags().add("C");
        tagForm.getTags().add("D");
        log.info("========================");

        tagService.createOrCountingTag(tagForm.getTags());
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

    @PostMapping("/product/delete")
    public String deleteProduct(@CurrentAccount Account account, @RequestParam("itemId") Item item) throws IllegalAccessException {
        if (!item.isMyItem(account)) {
            throw new IllegalAccessException("접근 권한이 없습니다.");
        }
        if (item.isReserved()) {
            log.info("상품을 삭제할 수 없습니다.");
            // TODO 에러 메시지를 Json으로 보낼지 ErrorMsg를 보낼지 결정해야 함
        }
        if (item.deletable()) {
            itemService.deleteItem(item);
        }
        return "redirect:/product/my-list";
    }

    // 테스트용
    @PostMapping("/product/tag")
    public String addNewTag(@CurrentAccount Account account, @RequestParam("itemId") Item item, @RequestParam("tag") String tag) {
        Set<String> tags = new HashSet<>();
        tags.add(tag);
        tagService.createOrCountingTag(tags);
        itemService.joinItemWithTags(item, tags);
        return "redirect:/product/edit/" + item.getId();
    }


    @PostMapping("/favorite/add")
    @ResponseBody
    public void addFavorite(@CurrentAccount Account account, @RequestParam("itemId") Item item) {
        itemService.addFavorite(account, item);
    }

    @PostMapping("/favorite/delete")
    @ResponseBody
    public void deleteFavorite(@CurrentAccount Account account, @RequestParam("itemId") Item item) {
        itemService.deleteFavorite(account, item);
    }
}
