package com.infogain.gcp.poc.poller.entity;

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
@Table(name = "OUTBOX_STATUS")
public class OutboxStatusEntity {
	
	private String locator ;
	private String version ;
	private String destination ;
	private int status ;
	private Timestamp created ;
	private Timestamp updated ;
	private String instance;
	
	 

}
