package com.himanshu.url_shortener_api.controllers;

import com.himanshu.url_shortener_api.dtos.UrlLongRequestDTO;
import com.himanshu.url_shortener_api.services.UrlService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlControllerTest {

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    @Test
    public void testShortenUrl_Success() {
        UrlLongRequestDTO requestDTO = new UrlLongRequestDTO("https://example.com", null);
        String shortUrl = "shortUrl";
        String expected = "/url-shortener/v1/".concat(shortUrl);
        when(urlService.convertToShortUrl(requestDTO)).thenReturn(shortUrl);
        ResponseEntity<HashMap<String, String>> response = urlController.shortenUrl(requestDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody().get("shortUrl"));
    }

    @Test
    public void testGetAndRedirect_Success() throws Exception {
        String shortUrl = "shortUrl";
        String longUrl = "https://example.com";
        when(urlService.getOriginalUrl(shortUrl)).thenReturn(longUrl);
        ResponseEntity<Void> response = urlController.getAndRedirect(shortUrl);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(URI.create(longUrl), response.getHeaders().getLocation());
    }

    @Test
    public void testGetAndRedirect_ExpiredLink() throws Exception {
        String shortUrl = "shortUrl";
        when(urlService.getOriginalUrl(shortUrl)).thenThrow(new EntityNotFoundException("This link has expired!"));
        ResponseEntity<Void> response = urlController.getAndRedirect(shortUrl);
        assertEquals(HttpStatus.GONE, response.getStatusCode());
    }

    @Test
    public void testGetAndRedirect_UrlNotFound() throws Exception {
        String shortUrl = "shortUrl";
        when(urlService.getOriginalUrl(shortUrl)).thenThrow(new EntityNotFoundException("No URL with short URL : " + shortUrl));
        ResponseEntity<Void> response = urlController.getAndRedirect(shortUrl);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetAndRedirect_InternalServerError() throws Exception {
        String shortUrl = "shortUrl";
        when(urlService.getOriginalUrl(shortUrl)).thenThrow(new RuntimeException("Internal server error"));
        ResponseEntity<Void> response = urlController.getAndRedirect(shortUrl);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
