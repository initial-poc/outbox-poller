package com.infogain.gcp.poc.poller.entity;

import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;

import com.google.cloud.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "outbox")
public class OutboxEntity {
	@PrimaryKey(keyOrder =1)
	private String locator;
	@PrimaryKey(keyOrder =2)
	private String version;
	@Column(name="parent_locator")
	private String parentLocator;
	private Timestamp created;
	private String data;

}
