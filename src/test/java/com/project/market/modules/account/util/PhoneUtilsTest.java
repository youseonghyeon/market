package com.project.market.modules.account.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PhoneUtilsTest {

    @Test
    void phoneTest() {
        assertEquals("010-1234-5678", PhoneUtils.format("01012345678"));
        assertEquals("010-123-4567", PhoneUtils.format("0101234567"));
    }


}
