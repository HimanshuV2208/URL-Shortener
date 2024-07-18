package com.himanshu.url_shortener_api.mappers;

import com.himanshu.url_shortener_api.dtos.UrlLongRequestDTO;
import com.himanshu.url_shortener_api.entities.Url;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class UrlMapper {
    public Url UrlLongRequestToUrl(UrlLongRequestDTO dto){
        return Url.builder().
                 longUrl(dto.getLongUrl())
                 .createdDate(new Date())
                 .expiryDate(dto.getExpiryDate())
                 .build();
    }
}
