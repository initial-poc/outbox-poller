package com.infogain.gcp.poc.component;

import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.infogain.gcp.poc.poller.service.PnrService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PollerExecutor {

    @Autowired
    private PnrService pollerPnrService;

     @Scheduled(cron = "*/10 * * * * *")
   // @Scheduled(cron = "0 * * * * *")
    public void process() {
        log.info("poller started at {}", LocalTime.now());
        pollerPnrService.processRecords();
        log.info("poller completed at {}", LocalTime.now());
    }

}
