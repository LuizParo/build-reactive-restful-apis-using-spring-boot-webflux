package com.learnreactivespring.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
public class SampleHandlerFunction {

    public Mono<ServerResponse> handleFlux(ServerRequest request) {
        log.info("receiving request: " + request);

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(Flux.just(1, 2, 3, 4).log(), Integer.class);
    }

    public Mono<ServerResponse> handleMono(ServerRequest request) {
        log.info("receiving request: " + request);

        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(Mono.just(1).log(), Integer.class);
    }
}
