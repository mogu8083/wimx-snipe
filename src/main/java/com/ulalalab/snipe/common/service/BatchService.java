package com.ulalalab.snipe.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j(topic = "HTTP.BatchService")
@RequiredArgsConstructor
public class BatchService {

    private final CommonService commonService;

    /**
     * Perform GC (3분)
     */
    @Scheduled(fixedRate = 1000 * 60 * 3)
    public void performGc() {
        log.info("Perform GC 실행");
        commonService.performGc();
    }
}