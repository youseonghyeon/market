package com.project.market.modules.delivery.controller;

import com.project.market.infra.exception.UnAuthorizedException;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.util.CurrentAccount;
import com.project.market.modules.delivery.repository.DeliveryRepository;
import com.project.market.modules.delivery.entity.Delivery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class TrackingController {

    private final DeliveryRepository deliveryRepository;

    @GetMapping("/shiptrack/{deliveryId}")
    public String shiptrackForm(@CurrentAccount Account account, @PathVariable("deliveryId") Delivery delivery, Model model) {
        if (!delivery.isOwner(account)) {
            throw new UnAuthorizedException("잘못된 접근");
        }
        model.addAttribute(delivery);
        return "delivery/shiptrack";
    }
}
