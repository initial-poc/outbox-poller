package com.infogain.gcp.poc.component;

import com.infogain.gcp.poc.poller.service.MessageGroupRecordProcessorService;
import com.infogain.gcp.poc.poller.service.OutboxRecordProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@Slf4j
@Component
@RequiredArgsConstructor
@RestController
public class FailedMessageGroupRecordPollerExector {
    private final MessageGroupRecordProcessorService messageGroupRecordProcessorService;

   /* @GetMapping("/processMessageGroupFailedRecords")
    public void processFailedRecords() {
        log.info("Failed Record poller started at {}", LocalTime.now());
        messageGroupRecordProcessorService.processFailedRecords();
        log.info("Failed Record poller completed at {}", LocalTime.now());
    }*/

    @GetMapping("/processMessageGroupStuckRecords")
    public void processStuckRecords() {
        log.info("Failed Record poller started at {}", LocalTime.now());
        messageGroupRecordProcessorService.processStuckRecords();
        log.info("Failed Record poller completed at {}", LocalTime.now());
    }
}
