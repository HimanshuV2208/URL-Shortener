package com.himanshu.url_shortener_api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UrlLongRequestDTO {
    private String longUrl;
    private Date expiryDate;
}
