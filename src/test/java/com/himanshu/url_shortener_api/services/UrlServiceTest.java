package com.himanshu.url_shortener_api.services;

import com.himanshu.url_shortener_api.dtos.UrlLongRequestDTO;
import com.himanshu.url_shortener_api.entities.Url;
import com.himanshu.url_shortener_api.mappers.UrlMapper;
import com.himanshu.url_shortener_api.repositories.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.InvalidPropertiesFormatException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    private static UrlLongRequestDTO dto;
    private static Url urlEntity;
    private static LocalDateTime currentDate;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlMapper urlMapper;
    @InjectMocks
    private UrlService urlService;

    @BeforeAll
    static void setup() {
        dto = new UrlLongRequestDTO();
        dto.setLongUrl("https://www.random-url.com");
        currentDate = LocalDateTime.now();

        dto.setExpiryDate(currentDate.plusHours(2));
        urlEntity = Url.builder()
                .id(Long.MAX_VALUE)
                .longUrl("https://www.random-url.com")
                .createdDate(currentDate.minusHours(1))
                .expiryDate(currentDate.plusHours(2)).build();
    }

    @Test
    void test_convertToShortUrl_valid() {
        when(urlMapper.urlLongRequestToUrl(dto)).thenReturn(urlEntity);
        when(urlRepository.save(urlEntity)).thenReturn(urlEntity);
        String shortUrl = urlService.convertToShortUrl(dto);
        assertEquals("sclqgMAPqi2", shortUrl);
        verify(urlMapper, times(1)).urlLongRequestToUrl(dto);
        verify(urlRepository, times(1)).save(urlEntity);
    }

    @Test
    void test_getOriginalUrl_valid() {
        String shortUrl = "sclqgMAPqi2";
        when(urlRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.ofNullable(urlEntity));
        String longUrl = null;
        try {
            longUrl = urlService.getOriginalUrl(shortUrl);
        } catch (InvalidPropertiesFormatException ignored) {
        }
        assertEquals("https://www.random-url.com", longUrl);
        verify(urlRepository, times(1)).findById(Long.MAX_VALUE);
    }
    @Test
    void test_getOriginalUrl_expired() {
        urlEntity.setExpiryDate(currentDate.minusHours(1));
        String shortUrl = "sclqgMAPqi2";
        when(urlRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.ofNullable(urlEntity));
        Exception exception = assertThrows(EntityNotFoundException.class, ()-> urlService.getOriginalUrl(shortUrl));
        assertEquals("This link has expired!", exception.getMessage());
        verify(urlRepository, times(1)).findById(Long.MAX_VALUE);
        urlEntity.setExpiryDate(currentDate.plusHours(2));
    }

    @Test
    void test_getOriginalUrl_notFound() {
        String shortUrl = "sclqgMAPqi2";
        when(urlRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, ()-> urlService.getOriginalUrl(shortUrl));
        assertEquals("No URL with short URL : " + shortUrl, exception.getMessage());
        verify(urlRepository, times(1)).findById(Long.MAX_VALUE);
    }
}