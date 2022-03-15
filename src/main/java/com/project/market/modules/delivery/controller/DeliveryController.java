package com.project.market.modules.delivery.controller;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.delivery.dao.DeliveryRepository;
import com.project.market.modules.delivery.entity.Delivery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryRepository deliveryRepository;


    @GetMapping("/shiptrack/{deliveryId}")
    public String shiptrackForm(@CurrentAccount Account account, @PathVariable("deliveryId") Delivery delivery,
                                Model model) throws IllegalAccessException {
        if (!delivery.checkOwnership(account)) {
            throw new IllegalAccessException("잘못된 접근");
        }
        model.addAttribute(delivery);
        return "delivery/shiptrack";
    }


}
