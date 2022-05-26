package com.project.market.modules.item.controller;

import com.project.market.infra.fileupload.AwsS3Service;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.item.service.ItemService;
import com.project.market.modules.item.service.TagService;
import com.project.market.modules.item.repository.FavoriteRepository;
import com.project.market.modules.item.repository.ItemRepository;
import com.project.market.modules.item.repository.TagRepository;
import com.project.market.modules.item.entity.Favorite;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.entity.Tag;
import com.project.market.modules.item.form.ItemForm;
import com.project.market.modules.item.validator.ItemFormValidator;
import com.project.market.modules.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
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
    private final FavoriteRepository favoriteRepository;
    private final AwsS3Service awsS3Service;


    @ExceptionHandler(IllegalStateException.class)
    public String ex(Exception e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "exception/item-ex";
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
        tagService.createOrCountingTag(itemForm.getTags());
        Item item = itemService.createNewItem(itemForm);
        itemForm.setId(item.getId());

        String dir = "item/" + item.getId() + "/";
        String coverPhotoUrl = awsS3Service.uploadFile(dir, itemForm.getCoverPhoto());
        String photoUrl = awsS3Service.uploadFile(dir, itemForm.getPhoto());

        itemService.savePhotoPath(item, coverPhotoUrl, photoUrl);

        // 알람 전송(비동기)
        notificationService.noticeItemEnrollment(item);
        // 메일 전송(비동기)
        notificationService.noticeByEmailItemEnrollment(item);
        return "redirect:/product/" + item.getId();
    }

    @GetMapping("/product/edit/{itemId}")
    public String editMyProduct(@CurrentAccount Account account, @PathVariable("itemId") Item item, Model model) {
        // 어드민만 진입 가능
        // TODO 가격 수정 시 Cart 리포지토리에 있는 totalPrice의 대규모 연산(수정)이 필요함 (Sync 작업을 해주어야 함)

        model.addAttribute("itemForm", modelMapper.map(item, ItemForm.class));
        model.addAttribute("tagList", item.getTags());
        return "products/edit";
    }

    @PostMapping("/product/modify")
    public String modifyProduct(@CurrentAccount Account account, @ModelAttribute ItemForm itemForm, Errors errors) {
        if (errors.hasErrors()) {
            return "products/edit";
        }
        Item item = itemRepository.findItemWithTagsById(itemForm.getId());
        // 어드민만 진입 가능

        tagService.createOrFindTags(itemForm.getTags());
        itemService.modifyItem(item, itemForm);
        return "redirect:/product/" + item.getId();
    }

    @PostMapping("/product/delete")
    public String deleteProduct(@CurrentAccount Account account, @RequestParam("itemId") Item item) {
        // 어드민만 진입 가능

        itemService.deleteItem(item);
        return "redirect:/product/modify-list";
    }

    @PostMapping("/favorite/toggle")
    @ResponseBody
    public String toggleFavorite(@CurrentAccount Account account, @RequestParam("itemId") Item item) {
        Favorite favorite = favoriteRepository.findByAccountAndItem(account, item);
        if (favorite != null) {
            itemService.deleteFavorite(favorite);
            return "delete";
        } else {
            itemService.addFavorite(account, item);
            return "add";
        }
    }
}
