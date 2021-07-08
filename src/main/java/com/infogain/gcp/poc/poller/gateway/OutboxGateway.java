package com.infogain.gcp.poc.poller.gateway;

import com.infogain.gcp.poc.poller.domainmodel.PNRModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxGateway {

	private final RestTemplate restTemplate;
	private final	WebClient webClient;


	@Value(value = "${serviceUrl}")
	private String seriveUrl;

	private String buildUrl() {
		return "http://" + seriveUrl + "/api/pnrs";
	}

	@Async("specificTaskExecutor")
	public void callServiceTemp(PNRModel req) {
		String response = null;
		try {
			RequestEntity<PNRModel> requestEntity = new RequestEntity<PNRModel>(req, HttpMethod.POST,
					new URI(buildUrl()));
			response = restTemplate.exchange(requestEntity, String.class).getBody();

		} catch (Exception ex) {
			log.error("Got the error from the service {}", ex.getMessage());

		}
		//return response;
	}

	public static ExecutorService taskExecutor() {
	return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}

	public Mono<String> callService(PNRModel req) {
		Mono<String> response = null;

		response = webClient.post().uri("/api/pnrs").body(Mono.just(req), PNRModel.class).retrieve()
				.onStatus((HttpStatus::isError), (it -> handleError(it.statusCode().getReasonPhrase())))
				.bodyToMono(String.class).publishOn(Schedulers.fromExecutor(taskExecutor()));

		log.info("service called");
		return response;
	}

	private Mono<Throwable> handleError(String reasonPhrase) {
		 log.info("Got the error while calling the service -> {}",reasonPhrase);
		return Mono.error(RuntimeException:: new);
	}
	 
}
