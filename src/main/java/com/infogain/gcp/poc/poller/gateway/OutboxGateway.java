package com.infogain.gcp.poc.poller.gateway;

import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.infogain.gcp.poc.poller.domainmodel.PNRModel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxGateway {

	private final RestTemplate restTemplate;
	private final	WebClient webClient;

	public String callServiceTemp(PNRModel req) {
		String response = null;
		try {
			RequestEntity<PNRModel> requestEntity = new RequestEntity<PNRModel>(req, HttpMethod.POST,
					new URI("http://localhost:9000/api/pnrs"));
			response = restTemplate.exchange(requestEntity, String.class).getBody();

		} catch (Exception ex) {
			log.error("Got the error from the service {}", ex.getMessage());

		}
		return response;
	}

	public static ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("outbox-");
		executor.initialize();
		return executor;
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
