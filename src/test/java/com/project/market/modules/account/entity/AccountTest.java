package com.project.market.modules.account.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void isExpiredPasswordToken() {
        boolean before = LocalDateTime.now().isBefore(LocalDateTime.now().minusMinutes(5));
        boolean after = LocalDateTime.now().isAfter(LocalDateTime.now().minusMinutes(5));
        System.out.println("before = " + before);
        System.out.println("after = " + after);

    }
}
