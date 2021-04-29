package com.infogain.gcp.poc.component;

import java.time.LocalTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.infogain.gcp.poc.poller.service.PnrService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class FailedRecordPollerExector {
	    private final PnrService pollerPnrService;

	     @Scheduled(cron = "*/50 * * * * *")
	    public void process() {
	        log.info("Failed Record poller started at {}", LocalTime.now());
	        pollerPnrService.processFailedRecords();
	        log.info("Failed Record poller completed at {}", LocalTime.now());
	    }
}
