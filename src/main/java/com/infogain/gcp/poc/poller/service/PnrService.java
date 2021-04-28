package com.infogain.gcp.poc.poller.service;

import java.net.InetAddress;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.data.spanner.core.SpannerOperations;
import org.springframework.stereotype.Service;

import com.google.cloud.spanner.Statement;
import com.google.common.base.Stopwatch;
import com.infogain.gcp.poc.poller.entity.OutboxEntity;
import com.infogain.gcp.poc.poller.repository.SpannerOutboxRepository;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

public class PnrService {

	private final OutboxService outboxStatusService;
	private final SpannerOutboxRepository spannerOutboxRepository;
	private final String ip;

	@Value(value = "${limit}")
	private int recordLimit;

	private static final String OUTBOX_SQL = "SELECT * FROM OUTBOX WHERE STATUS IN (0,4)";

	@Autowired
	@SneakyThrows
	public PnrService(OutboxService outboxStatusService, SpannerOutboxRepository spannerOutboxRepository) {
		this.outboxStatusService = outboxStatusService;
		this.spannerOutboxRepository = spannerOutboxRepository;
		ip = InetAddress.getLocalHost().getHostAddress();
	}

	public void processRecords() {

		log.info("Getting record to process by application->  {}", ip);
		Stopwatch stopWatch = Stopwatch.createStarted();
		SpannerOperations spannerTemplate = spannerOutboxRepository.getSpannerTemplate();
		List<OutboxEntity> recordToProcess = spannerTemplate.query(OutboxEntity.class,
				Statement.of(String.format(OUTBOX_SQL, recordLimit)), null);
		stopWatch.stop();
		log.info("Total time taken to fetch the records {}", stopWatch);
		if (recordToProcess.isEmpty()) {
			log.info("No Record to process");
			return;
		}
		log.info("total record - {} to process by application->  {}", recordToProcess.size(), ip);
		log.info("RECORD {}", recordToProcess);

		process(recordToProcess);
	}

	private void process(List<OutboxEntity> outboxEntities) {
		outboxEntities.stream().forEach(outboxStatusService::processRecord);
	}

}
