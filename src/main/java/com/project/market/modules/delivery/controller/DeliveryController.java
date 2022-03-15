package com.project.market.modules.delivery.controller;

import com.project.market.modules.delivery.dao.DeliveryRepository;
import com.project.market.modules.delivery.dto.CompleteDto;
import com.project.market.modules.delivery.entity.Delivery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/delivery")
public class DeliveryController {

    private final DeliveryRepository deliveryRepository;

    @GetMapping("/list")
    public String deliveryHome(Model model) {
        List<Delivery> deliveryList = deliveryRepository.findAll();
        model.addAttribute(deliveryList);
        return "delivery/list";
    }

    @PostMapping("/complete")
    @ResponseBody
    public String completeDelivery(@RequestBody CompleteDto completeDto) {
        Delivery delivery = deliveryRepository.findById(completeDto.getDeliveryId()).orElseThrow();
        delivery.shippingComplete();
        return "ok";
    }

    @PostMapping("/cancel")
    @ResponseBody
    public String cancelDelivery(@RequestBody CompleteDto completeDto) {
        Delivery delivery = deliveryRepository.findById(completeDto.getDeliveryId()).orElseThrow();
        delivery.shippingCancel();
        return "ok";
    }





}
