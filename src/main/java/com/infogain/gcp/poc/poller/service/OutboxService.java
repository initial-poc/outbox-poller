package com.infogain.gcp.poc.poller.service;

import java.net.InetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infogain.gcp.poc.poller.entity.OutboxEntity;
import com.infogain.gcp.poc.poller.gateway.OutboxGateway;
import com.infogain.gcp.poc.poller.repository.SpannerOutboxRepository;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class OutboxService {
	private static final String NEW_STATUS = "0";
	private static final String COMPLETE_STATUS = "1";
	private static final String FAILURE_STATUS = "2";

	private final String ip;

	private final SpannerOutboxRepository outboxRepository;
	private final OutboxGateway gateway;

	@Autowired
	@SneakyThrows
	public OutboxService(SpannerOutboxRepository outboxRepository, OutboxGateway gateway) {
		ip = InetAddress.getLocalHost().getHostAddress();
		this.outboxRepository = outboxRepository;
		this.gateway = gateway;
	}

	public void processRecord(OutboxEntity outboxEntity) {
		updateRecord(outboxEntity, COMPLETE_STATUS);

		Mono<String> responseBody = gateway.callService(outboxEntity.buildModel());

		responseBody.doOnError(exp -> {
			log.info("on Error {}", exp.getMessage());
			updateRecord(outboxEntity, FAILURE_STATUS);
		}).subscribe(s -> log.info("Got the response -> {}", s));

	}

	private void updateRecord(OutboxEntity entity, String status) {
		entity.setStatus(status);
		log.info("Going to update status for the record {}", entity);
		outboxRepository.save(entity);
	}

}
