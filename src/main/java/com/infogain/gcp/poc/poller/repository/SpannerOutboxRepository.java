package com.infogain.gcp.poc.poller.repository;

import org.springframework.cloud.gcp.data.spanner.repository.SpannerRepository;

import com.infogain.gcp.poc.poller.entity.OutboxEntity;

public interface SpannerOutboxRepository extends SpannerRepository<OutboxEntity	, String> { }
