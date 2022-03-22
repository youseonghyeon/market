package com.project.market.modules.delivery.controller;

import com.project.market.modules.delivery.dao.DeliveryRepository;
import com.project.market.modules.delivery.dao.DeliveryService;
import com.project.market.modules.delivery.dto.CompleteDto;
import com.project.market.modules.delivery.entity.Delivery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/delivery")
public class DeliveryController {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryService deliveryService;

    @GetMapping("/list")
    public String deliveryHome(Model model) {
        List<Delivery> deliveryList = deliveryRepository.findAll();
        model.addAttribute("deliveryList", deliveryList);
        return "delivery/list";
    }

    @PostMapping("/complete")
    @ResponseBody
    @ResponseStatus(code = HttpStatus.OK)
    public String completeDelivery(@RequestBody CompleteDto completeDto) {
        log.info("complete 실행");
        Delivery delivery = deliveryRepository.findById(completeDto.getDeliveryId()).orElseThrow();
        if (delivery.isShipped()) {
            throw new IllegalStateException("배송이 이미 완료되었습니다.");
        }
        deliveryService.competeDelivery(delivery);
        log.info("complete 성공");
        return "ok";
    }

    @PostMapping("/cancel")
    @ResponseBody
    @ResponseStatus(code = HttpStatus.OK)
    public String cancelDelivery(@RequestBody CompleteDto completeDto) {
        Delivery delivery = deliveryRepository.findById(completeDto.getDeliveryId()).orElseThrow();
        delivery.shippingCancel();
        return "ok";
    }
}
