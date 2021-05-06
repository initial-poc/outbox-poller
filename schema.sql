
CREATE TABLE OUTBOX (  locator STRING(6) NOT NULL,  version INT64 NOT NULL,  parent_locator STRING(6),
created TIMESTAMP NOT NULL,  data STRING(MAX) NOT NULL,  status INT64 NOT NULL,
retry_count INT64,  updated TIMESTAMP NOT NULL OPTIONS (    allow_commit_timestamp = true  ),
processing_time_millis INT64,) PRIMARY KEY(locator, version);
CREATE TABLE group_message_store (  pnrid STRING(MAX),  messageseq STRING(MAX),
status INT64,  payload STRING(MAX),  timestamp TIMESTAMP,  instance STRING(MAX),  retry_count INT64,
updated TIMESTAMP OPTIONS (    allow_commit_timestamp = true  ),) PRIMARY KEY(pnrid, messageseq);
CREATE UNIQUE INDEX pnr_index ON group_message_store(pnrid, messageseq, status);
