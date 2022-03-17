package com.project.market.modules.delivery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.market.WithAccount;
import com.project.market.modules.delivery.dao.DeliveryRepository;
import com.project.market.modules.delivery.dao.DeliveryService;
import com.project.market.modules.delivery.dto.CompleteDto;
import com.project.market.modules.delivery.entity.Delivery;
import com.project.market.modules.delivery.entity.DeliveryMethod;
import com.project.market.modules.delivery.entity.DeliveryStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class DeliveryControllerTest {

    @Autowired
    DeliveryRepository deliveryRepository;
    @Autowired
    DeliveryService deliveryService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    private Long TEST_DELIVERY_ID;

    @BeforeEach
    public void init() {
        Delivery delivery = Delivery.builder()
                .accountId(999L)
                .fee(2500)
                .originAddress("test-origin")
                .destinationAddress("test-destination")
                .expectedArrivalFrom(LocalDate.now().plus(2, ChronoUnit.DAYS))
                .expectedArrivalUntil(LocalDate.now().plus(3, ChronoUnit.DAYS))
                .deliveryMethod(DeliveryMethod.POST)
                .deliveryStatus(DeliveryStatus.READY)
                .shippingCompany("test-company")
                .shippingCode("aaa-bbb-ccc")
                .trackingNumber("11001100")
                .trackingUrl("www.")
                .build();
        Delivery savedDelivery = deliveryRepository.save(delivery);
        TEST_DELIVERY_ID = savedDelivery.getId();
    }

    @AfterEach
    public void delete() {
        deliveryRepository.deleteAll();
    }

    @Test
    @WithAccount("testUser")
    void deliveryHome() throws Exception {
        mockMvc.perform(get("/delivery/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("delivery/list"))
                .andExpect(model().attributeExists("deliveryList"));
    }

    @Test
    @WithAccount("testUser")
    void completeDelivery() throws Exception {
        CompleteDto completeDto = new CompleteDto();
        completeDto.setDeliveryId(TEST_DELIVERY_ID);
        mockMvc.perform(post("/delivery/complete")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(completeDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));

        Delivery delivery = deliveryRepository.findById(TEST_DELIVERY_ID).orElseThrow();
        assertTrue(delivery.isShipped());
    }

    @Test
    @WithAccount("testUser")
    void cancelDeliverySuccess() throws Exception {
        CompleteDto completeDto = new CompleteDto();
        completeDto.setDeliveryId(TEST_DELIVERY_ID);
        mockMvc.perform(post("/delivery/cancel")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(completeDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));

        Delivery delivery = deliveryRepository.findById(TEST_DELIVERY_ID).orElseThrow();
        assertEquals(delivery.getDeliveryStatus(), DeliveryStatus.CANCEL);
    }

//    @Test
//    @WithAccount("testUser")
//    void cancelDeliveryFail() throws Exception{
//        CompleteDto completeDto = new CompleteDto();
//        completeDto.setDeliveryId(TEST_DELIVERY_ID);
//        Delivery delivery = deliveryRepository.findById(TEST_DELIVERY_ID).orElseThrow();
//        delivery.shippingComplete();
//
//        mockMvc.perform(post("/delivery/cancel")
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(objectMapper.writeValueAsString(completeDto))
//                        .with(csrf()))
////                        .andExpect(예외 처리)
//
//        assertEquals(delivery.getDeliveryStatus(), DeliveryStatus.CANCEL);
//    }
}
