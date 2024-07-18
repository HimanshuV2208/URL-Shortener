package com.himanshu.url_shortener_api.repositories;

import com.himanshu.url_shortener_api.entities.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
}
