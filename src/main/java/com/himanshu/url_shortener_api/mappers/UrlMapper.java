package com.himanshu.url_shortener_api.mappers;

import com.himanshu.url_shortener_api.dtos.UrlLongRequestDTO;
import com.himanshu.url_shortener_api.entities.Url;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UrlMapper {
    public Url urlLongRequestToUrl(UrlLongRequestDTO dto) {
        return Url.builder()
                .longUrl(dto.getLongUrl())
                .createdDate(LocalDateTime.now())
                .expiryDate(dto.getExpiryDate())
                .build();
    }
}
