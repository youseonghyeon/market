package com.project.market.modules.product.controller;

import com.project.market.modules.product.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.InvalidParameterException;

@Slf4j
@Controller
public class ProductController {

    @GetMapping("/products/{productId}")
    public String productForm(@PathVariable("productId") Product product, Model model) {
        if (product == null) {
            throw new InvalidParameterException("존재하지 않는 상품입니다.");
        }
        log.info("product.getName()={}", product.getName());
        model.addAttribute(product);
        return "products/product";
    }
}
