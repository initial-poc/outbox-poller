package com.infogain.gcp.poc.poller.service;

import org.springframework.stereotype.Service;

import com.infogain.gcp.poc.poller.entity.OutboxEntity;
import com.infogain.gcp.poc.poller.gateway.OutboxGateway;
import com.infogain.gcp.poc.poller.repository.SpannerOutboxRepository;
import com.infogain.gcp.poc.util.RecordStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxService {


	private final SpannerOutboxRepository outboxRepository;
	private final OutboxGateway gateway;

	 

	public void processRecord(OutboxEntity outboxEntity) {
		updateRecord(outboxEntity, RecordStatus.IN_PROGESS.getStatusCode());

		Mono<String> responseBody = gateway.callService(outboxEntity.buildModel());

		responseBody.doOnError(exp -> {
			log.info("on Error {}", exp.getMessage());
			updateRecord(outboxEntity, RecordStatus.FAILED.getStatusCode());
		}).subscribe(s -> {
			log.info("Got the response -> {}", s);
			updateRecord(outboxEntity, RecordStatus.COMPLETED.getStatusCode());
		});

	}

	private void updateRecord(OutboxEntity entity, int status) {
		entity.setStatus(status);
		log.info("Going to update status for the record {}", entity);
		outboxRepository.save(entity);
	}

}
