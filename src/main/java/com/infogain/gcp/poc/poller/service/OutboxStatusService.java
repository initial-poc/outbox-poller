package com.infogain.gcp.poc.poller.service;

import java.net.InetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.data.spanner.core.SpannerOperations;
import org.springframework.stereotype.Service;

import com.google.cloud.Timestamp;
import com.infogain.gcp.poc.poller.entity.OutboxEntity;
import com.infogain.gcp.poc.poller.entity.OutboxStatusEntity;
import com.infogain.gcp.poc.poller.repository.SpannerOutBoxStatusRepository;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OutboxStatusService  {
	private static final int NEW_STATUS=0;
	private static final int PROCESSING_STATUS=1;
	private static final int COMPLETE_STATUS=2;
	private final String ip ;
	
	private final SpannerOutBoxStatusRepository outBoxStatusRepository;
	 
	 
	
	@Autowired
	@SneakyThrows
	public OutboxStatusService( SpannerOutBoxStatusRepository outBoxStatusRepository) {
		ip= InetAddress.getLocalHost().getHostAddress();
		this.outBoxStatusRepository = outBoxStatusRepository;
	}
	
	public OutboxStatusEntity processRecord(OutboxEntity outboxEntity) {

		OutboxStatusEntity outboxStatusEntity = OutboxStatusEntity.builder().version(outboxEntity.getVersion()).locator(outboxEntity.getLocator()).status(PROCESSING_STATUS).destination("TTY").instance(ip).created(Timestamp.now()).updated(Timestamp.now()).build();
		log.info("Going to process record {}",outboxStatusEntity);
		SpannerOperations template = outBoxStatusRepository.getSpannerTemplate();
		template
		.insert(outboxStatusEntity);
		return outboxStatusEntity;
	}
	
	
	public void completeRecord(OutboxStatusEntity processingRecord) {
		OutboxStatusEntity completeTaskStatus = OutboxStatusEntity.builder().created(processingRecord.getCreated()).destination(processingRecord.getDestination()).instance(ip).locator(processingRecord.getLocator()).status(COMPLETE_STATUS).updated(Timestamp.now()).version(processingRecord.getVersion()).build();
		log.info("Completed record {} by application-> {} ",completeTaskStatus,ip);
		outBoxStatusRepository.save(completeTaskStatus);
	}


	
}
