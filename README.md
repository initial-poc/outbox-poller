# Google cloud PNR Order POC

* Clone maven project using below command
```
git clone https://github.com/initial-poc/spanner-poller.git
```

* Build the code using below command
```
mvn clean package
```

# Google Cloud Spanner DB Configuration
```
instance = instance-1
database id = database-1
project name = pnr-order-poc
```


# Spanner DDL scripts
* OUTBOX table
```
CREATE TABLE OUTBOX (
	locator STRING(6) NOT NULL,
	version INT64 NOT NULL,
	parent_locator STRING(6),
	created TIMESTAMP NOT NULL,
	data STRING(MAX) NOT NULL,
) PRIMARY KEY (locator, version)
```

* OUTBOX_STATUS table
```
CREATE TABLE OUTBOX_STATUS (
	locator STRING(6) NOT NULL,
	version INT64 NOT NULL,
	destination STRING(20) NOT NULL,
	status INT64 NOT NULL,
	created TIMESTAMP NOT NULL,
	updated TIMESTAMP NOT NULL,
	instance STRING(64) NOT NULL,
	FOREIGN KEY (locator, version) REFERENCES OUTBOX(locator, version),
) PRIMARY KEY (locator, version, destination),
INTERLEAVE IN PARENT OUTBOX ON DELETE CASCADE
```



* processing_locators index
```
CREATE INDEX processing_locators 
ON OUTBOX_STATUS (
	status,
	locator
)
```
 
# How to run application

* There is runtime param limit, it is used to limit the record set.
```
java -jar -Dlimit=5 pnr-order-poc.jar
```