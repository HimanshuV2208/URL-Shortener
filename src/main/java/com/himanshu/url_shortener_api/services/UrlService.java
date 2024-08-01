package com.himanshu.url_shortener_api.services;

import com.himanshu.url_shortener_api.dtos.UrlLongRequestDTO;
import com.himanshu.url_shortener_api.entities.Url;
import com.himanshu.url_shortener_api.mappers.UrlMapper;
import com.himanshu.url_shortener_api.repositories.UrlRepository;
import com.himanshu.url_shortener_api.utils.BaseConversionUtil;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.InvalidPropertiesFormatException;

@Service
public class UrlService {

    private static final Logger logger = LoggerFactory.getLogger(UrlService.class);
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;

    public UrlService(UrlRepository urlRepository, UrlMapper urlMapper) {
        this.urlRepository = urlRepository;
        this.urlMapper = urlMapper;
    }

    public String convertToShortUrl(UrlLongRequestDTO urlLongRequestDTO) {
        logger.debug(String.format("Service : DTO received -> %s", urlLongRequestDTO));
        Url mappedUrl = urlMapper.urlLongRequestToUrl(urlLongRequestDTO);
        logger.debug(String.format("Service : Mapped to Entity -> %s", mappedUrl));
        Url urlSavedInDb = urlRepository.save(mappedUrl);
        logger.debug(String.format("Service : Saved in DB -> %s", urlSavedInDb));
        String encodedUrl = BaseConversionUtil.IdToB62EncodedString(urlSavedInDb.getId());
        logger.debug(String.format("Service : Corresponding encoded URL -> %s", encodedUrl));
        return encodedUrl;
    }

    public String getOriginalUrl(String shortUrl) throws InvalidPropertiesFormatException {
        logger.debug(String.format("Service : Received short URL -> %s", shortUrl));
        Long id = BaseConversionUtil.B62EncodedStringToId(shortUrl);
        logger.debug(String.format("Service : Received short URL -> %d", id));
        Url urlFromDb = urlRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No URL with short URL : " + shortUrl));
        logger.debug(String.format("Service : Retrieved URL from DB -> %s", urlFromDb));
        boolean isExpired = urlFromDb.getExpiryDate() != null && urlFromDb.getExpiryDate().isBefore(LocalDateTime.now());
        logger.debug(String.format("Service : ExpiryDate -> %s | CurrentDate -> %s | isExpired -> %b", urlFromDb.getExpiryDate(), LocalDateTime.now(), isExpired));
        if (isExpired) {
            logger.debug("Service : Link Expired");
            urlRepository.delete(urlFromDb);
            throw new EntityNotFoundException("This link has expired!");
        }
        logger.debug(String.format("Service : Returned long URL -> %s", urlFromDb.getLongUrl()));
        return urlFromDb.getLongUrl();
    }

}
