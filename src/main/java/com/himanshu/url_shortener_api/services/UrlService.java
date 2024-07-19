package com.himanshu.url_shortener_api.services;

import com.himanshu.url_shortener_api.dtos.UrlLongRequestDTO;
import com.himanshu.url_shortener_api.entities.Url;
import com.himanshu.url_shortener_api.mappers.UrlMapper;
import com.himanshu.url_shortener_api.repositories.UrlRepository;
import com.himanshu.url_shortener_api.utils.BaseConversionUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.InvalidPropertiesFormatException;

@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;

    public UrlService(UrlRepository urlRepository, UrlMapper urlMapper) {
        this.urlRepository = urlRepository;
        this.urlMapper = urlMapper;
    }

    public String convertToShortUrl(UrlLongRequestDTO urlLongRequestDTO) {
        Url mappedUrl = urlMapper.urlLongRequestToUrl(urlLongRequestDTO);
        Url urlSavedInDb = urlRepository.save(mappedUrl);
        String encodedUrl = BaseConversionUtil.IdToB62EncodedString(urlSavedInDb.getId());
        return encodedUrl;
    }

    public String getOriginalUrl(String shortUrl) throws InvalidPropertiesFormatException {
        Long id = BaseConversionUtil.B62EncodedStringToId(shortUrl);
        Url urlFromDb = urlRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No URL with short URL : " + shortUrl));
        boolean isExpired = urlFromDb.getExpiryDate() != null && urlFromDb.getExpiryDate().isBefore(LocalDateTime.now());
        if (isExpired) {
            throw new EntityNotFoundException("This link has expired!");
        }
        return urlFromDb.getLongUrl();
    }

}
