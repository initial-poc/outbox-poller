package com.infogain.gcp.poc.poller.domainmodel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PNRModel {

	private String pnrid;
	private String messageseq;
	private String payload;
	private String timestamp;
	
	 

}
