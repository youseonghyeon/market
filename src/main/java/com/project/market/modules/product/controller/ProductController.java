package com.project.market.modules.product.controller;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.product.dao.ProductService;
import com.project.market.modules.product.entity.Product;
import com.project.market.modules.product.form.ProductForm;
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
public class ProductController {

    private final ProductService productService;

    @GetMapping("/enroll")
    public String productEnrollForm(Model model) {
        model.addAttribute(new ProductForm());
        return "products/enroll";
    }

    @PostMapping("/enroll")
    public String productEnroll(@CurrentAccount Account account, @Valid ProductForm productForm,
                                Errors errors) {
        if (errors.hasErrors()) {
            return "products/enroll";
        }
        Product product = productService.createNewProduct(account, productForm);

        return "redirect:/products/" + product.getId();
    }

    @GetMapping("/{productId}")
    public String productForm(@PathVariable("productId") Product product, Model model) {
        if (product == null) {
            throw new InvalidParameterException("존재하지 않는 상품입니다.");
        }
        model.addAttribute(product);
        return "products/product";
    }
}
