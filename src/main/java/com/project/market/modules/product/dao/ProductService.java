package com.project.market.modules.product.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.product.entity.Product;
import com.project.market.modules.product.form.ProductForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createNewProduct(Account account, ProductForm productForm) {
        Product product = Product.builder()
                .name(productForm.getName())
                .price(productForm.getPrice())
                .quantity(productForm.getQuantity())
                .coverPhoto(productForm.getCoverPhoto())
                .photo(productForm.getPhoto())
                .enrolledDateTime(LocalDateTime.now())
                .enrolledBy(account)
                .build();

        return productRepository.save(product);
    }
}
