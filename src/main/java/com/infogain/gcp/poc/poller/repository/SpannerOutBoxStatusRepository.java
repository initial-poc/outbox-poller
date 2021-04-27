package com.infogain.gcp.poc.poller.repository;

import org.springframework.cloud.gcp.data.spanner.repository.SpannerRepository;

import com.infogain.gcp.poc.poller.entity.OutboxStatusEntity;

public interface SpannerOutBoxStatusRepository extends SpannerRepository<OutboxStatusEntity	, String> {
	
	
}
