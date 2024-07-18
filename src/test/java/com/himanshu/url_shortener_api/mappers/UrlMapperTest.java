package com.himanshu.url_shortener_api.mappers;

import com.himanshu.url_shortener_api.dtos.UrlLongRequestDTO;
import com.himanshu.url_shortener_api.entities.Url;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UrlMapperTest {

    private final UrlMapper urlMapper = new UrlMapper();

    @Test
    void test_urlLongRequestToUrl_validRequest() {
        String longUrl = "https://www.random-address.com";
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime currentDatePlus1Second = currentDate.plusSeconds(1);
        LocalDateTime currentDatePlus1Hr = LocalDateTime.now().plusHours(5);
        UrlLongRequestDTO dto = new UrlLongRequestDTO(longUrl, currentDatePlus1Hr);
        Url mappedUrl = urlMapper.urlLongRequestToUrl(dto);
        boolean isWithin1Second = mappedUrl.getCreatedDate().isBefore(currentDatePlus1Second);
        assertTrue(isWithin1Second);
        assertEquals(longUrl, mappedUrl.getLongUrl());
        assertEquals(currentDatePlus1Hr, mappedUrl.getExpiryDate());
    }
}