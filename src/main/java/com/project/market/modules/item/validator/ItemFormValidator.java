package com.project.market.modules.item.validator;

import com.project.market.modules.item.form.ItemForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Slf4j
@Component
public class ItemFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(ItemForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ItemForm itemForm = (ItemForm) target;

        // 직거래/배송거래 모두 선택을 안할 시 error 리턴
        if (!itemForm.getPostMethod() || !itemForm.getDirectMethod()) {
            log.warn("직거래 또는 배송거래 중 하나는 선택해야 합니다.");
            errors.rejectValue("method", "", "직거래 또는 배송거래 중 하나는 선택해야 합니다.");
        }
    }
}
