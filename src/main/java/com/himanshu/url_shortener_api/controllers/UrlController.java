package com.himanshu.url_shortener_api.controllers;

import com.himanshu.url_shortener_api.dtos.UrlLongRequestDTO;
import com.himanshu.url_shortener_api.services.UrlService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;

@RestController
@RequestMapping("/url-shortener/v1")
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<HashMap<String, String>> shortenUrl(@RequestBody UrlLongRequestDTO urlLongRequestDTO) {
        String shortenedUrl = "/url-shortener/v1/".concat(urlService.convertToShortUrl(urlLongRequestDTO));
        HashMap<String, String> map = new HashMap<>();
        map.put("shortUrl", shortenedUrl);
        return ResponseEntity.ok(map);
    }

    @GetMapping("/{shortUrl}")
    @Cacheable(value = "urls", key = "#shortUrl", sync = true)
    public ResponseEntity<Void> getAndRedirect(@PathVariable String shortUrl) {
        String longUrl = null;
        try {
            longUrl = urlService.getOriginalUrl(shortUrl);
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(longUrl)).build();
        } catch (EntityNotFoundException e) {
            ResponseEntity<Void> responseEntity = null;
            if (e.getMessage().equals("This link has expired!"))
                responseEntity = ResponseEntity.status(HttpStatus.GONE).build();
            else if (e.getMessage().equals("No URL with short URL : " + shortUrl))
                responseEntity = ResponseEntity.notFound().build();
            return responseEntity;
        } catch (InvalidPropertiesFormatException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
