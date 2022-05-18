package com.project.market.modules.delivery.controller;

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
        if (!deliveryRepository.isMyDelivery(account.getId(), delivery.getId())) {
            throw new IllegalStateException("고객님이 주문한 상품이 아닙니다.");
        }
        model.addAttribute(delivery);
        return "delivery/shiptrack";
    }
}
