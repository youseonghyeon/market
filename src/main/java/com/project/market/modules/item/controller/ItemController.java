package com.project.market.modules.item.controller;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.item.dao.ItemService;
import com.project.market.modules.item.entity.Item;
import com.project.market.modules.item.form.ItemForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.InvalidParameterException;

@Slf4j
@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/enroll")
    public String productEnrollForm(Model model) {
        model.addAttribute(new ItemForm());
        return "products/enroll";
    }

    @PostMapping("/enroll")
    public String productEnroll(@CurrentAccount Account account, @Valid ItemForm itemForm,
                                Errors errors) {
        if (errors.hasErrors()) {
            return "products/enroll";
        }
        Item item = itemService.createNewItem(account, itemForm);

        return "redirect:/products/deal/" + item.getId();
    }

    @GetMapping("/deal/{itemId}")
    public String productForm(@PathVariable("itemId") Item item, Model model) {
        if (item == null) {
            throw new InvalidParameterException("존재하지 않는 상품입니다.");
        }
        model.addAttribute(item);
        return "products/product";
    }


}