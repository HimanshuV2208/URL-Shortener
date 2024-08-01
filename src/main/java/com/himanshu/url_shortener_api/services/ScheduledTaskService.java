package com.himanshu.url_shortener_api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduledTaskService {

    private static final long CLEANUP_FREQUENCY = 120000; // in millis -> 2 minutes
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Scheduled(fixedRate = CLEANUP_FREQUENCY)
    @Transactional
    public void runSqlScript() {
        String sql = "DELETE FROM url WHERE expiry_date < NOW()";
        jdbcTemplate.execute(sql);
        logger.info("Executed SQL script to clean up old URLs.");
    }
}
