package com.infogain.gcp.poc.poller.service;

import java.net.InetAddress;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.data.spanner.core.SpannerOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.cloud.spanner.Statement;
import com.google.common.base.Stopwatch;
import com.infogain.gcp.poc.poller.entity.OutboxEntity;
import com.infogain.gcp.poc.poller.entity.OutboxStatusEntity;
import com.infogain.gcp.poc.poller.repository.SpannerOutboxRepository;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

public class PnrService {

	private final OutboxStatusService outboxStatusService;
	private final SpannerOutboxRepository spannerOutboxRepository;
	private final String ip ;
	
 
	
	@Value(value="${limit}")
	private int recordLimit;
	
	private static final String OUTBOX_SQL="SELECT o.* FROM OUTBOX o LEFT JOIN OUTBOX_STATUS os USING(locator,version) WHERE (os.status is NULL OR os.status = 0) and o.locator not IN ( select locator from OUTBOX where locator in (select distinct locator from OUTBOX_STATUS where status=1) or parent_locator in (select distinct locator from OUTBOX_STATUS where status=1) UNION ALL select parent_locator from OUTBOX where parent_locator is not null AND locator in (select distinct locator from OUTBOX_STATUS where status=1) or parent_locator in (select distinct locator from OUTBOX_STATUS where status=1)) ORDER BY o.created LIMIT %s";
@Autowired
@SneakyThrows
	public PnrService(OutboxStatusService outboxStatusService, SpannerOutboxRepository spannerOutboxRepository) {
		this.outboxStatusService = outboxStatusService;
		this.spannerOutboxRepository = spannerOutboxRepository;
		ip= InetAddress.getLocalHost().getHostAddress();
	}


 
	public void processRecords() {
		
		log.info("Getting record to process by application->  {}", ip);
		Stopwatch stopWatch = Stopwatch.createStarted();
		SpannerOperations spannerTemplate = spannerOutboxRepository.getSpannerTemplate();
		List<OutboxEntity> recordToProcess = spannerTemplate.query(OutboxEntity.class, Statement.of(String.format(OUTBOX_SQL,recordLimit)),null);
		stopWatch.stop();
		log.info("Total time taken to fetch the records {}",stopWatch);
		if (recordToProcess.isEmpty()) {
			log.info("No Record to process");
			return;
		}
		log.info("total record - {} to process by application->  {}", recordToProcess.size(), ip);
		log.info("RECORD {}", recordToProcess);

		process(recordToProcess);
	}

	@Transactional(rollbackFor = Exception.class)
	private void process(List<OutboxEntity> outboxEntities) {
		outboxEntities.stream().forEach(outboxEntity -> {
			OutboxStatusEntity processingRecord = outboxStatusService.processRecord(outboxEntity);
			outboxStatusService.completeRecord(processingRecord);

		});
	}

}
