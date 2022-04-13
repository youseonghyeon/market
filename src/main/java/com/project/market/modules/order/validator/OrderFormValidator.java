package com.project.market.modules.order.validator;

import com.project.market.modules.order.form.OrderForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
@RequiredArgsConstructor
public class OrderFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(OrderForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        OrderForm orderForm = (OrderForm) target;

    }
}
