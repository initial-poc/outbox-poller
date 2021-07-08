package com.infogain.gcp.poc.component;

import com.infogain.gcp.poc.poller.service.OutboxRecordProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Slf4j
@Component
public class OutboxPollerExecutor {

    @Autowired
    private OutboxRecordProcessorService pollerOutboxRecordProcessorService;

    public void process() {
        while (true) {
            log.info("poller started at {}", LocalTime.now());
            long nextPollerExecutionInterval = pollerOutboxRecordProcessorService.processRecords();
            try {
                Thread.sleep(nextPollerExecutionInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("poller completed at {}", LocalTime.now());
        }
    }
}






