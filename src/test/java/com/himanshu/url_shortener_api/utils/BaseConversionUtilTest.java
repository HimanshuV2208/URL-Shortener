package com.himanshu.url_shortener_api.utils;

import org.junit.jupiter.api.Test;

import java.util.InvalidPropertiesFormatException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BaseConversionUtilTest {

    @Test
    void test_idToB62EncodedString_lessThan62() {
        assertEquals("P", BaseConversionUtil.IdToB62EncodedString(5L));
    }

    @Test
    void test_idToB62EncodedString_greaterThan62() {
        // 352 % 62 -> 42 charAt(42) -> a
        // 5 % 62 -> 5 charAt(5) -> P
        String actual = BaseConversionUtil.IdToB62EncodedString(352L);
        assertEquals("Pa", actual);
    }

    @Test
    void test_idToB62EncodedString_veryLargeId() {
        String actual = BaseConversionUtil.IdToB62EncodedString(Long.MAX_VALUE);
        assertEquals("sclqgMAPqi2", actual);
    }

    @Test
    void test_idToB62EncodedString_zeroId() {
        String actual = BaseConversionUtil.IdToB62EncodedString(0L);
        assertEquals("M", actual);
    }

    @Test
    void test_b62EncodedStringToId_maxValue() {
        Long actual = null;
        try {
            actual = BaseConversionUtil.B62EncodedStringToId("sclqgMAPqi2");
        } catch (InvalidPropertiesFormatException ignored) {}
        assertEquals(Long.MAX_VALUE, actual);
    }

    @Test
    void test_b62EncodedStringToId_extremelyLargeString() {
        Exception exception = assertThrows(InvalidPropertiesFormatException.class, () -> BaseConversionUtil.B62EncodedStringToId("bbsdhjadwasdasdacascascascadcdscuii3g7uwc8hw8ecw89"));
        assertEquals("String is too long", exception.getMessage());
    }

    @Test
    void test_b62EncodedStringToId_normalString() {
        Long actual = null;
        try {
            actual = BaseConversionUtil.B62EncodedStringToId("Pa");
        } catch (InvalidPropertiesFormatException ignored) {}
        assertEquals(352, actual);
    }

    @Test
    void test_b62EncodedStringToId_maxValuePlus1() {
        Exception exception = assertThrows(InvalidPropertiesFormatException.class, () -> BaseConversionUtil.B62EncodedStringToId("sclqgMAPqi3"));
        assertEquals("String is too long", exception.getMessage());
    }
}