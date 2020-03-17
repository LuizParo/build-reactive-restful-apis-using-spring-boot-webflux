package com.learnreactivespring.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    @Test
    public void fluxErrorHandling() {
        final Flux<String> flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("error")))
                .concatWith(Flux.just("D")) // won't be called
                .onErrorResume(error -> {
                    error.printStackTrace();
                    return Flux.just("default in case of error");
                })
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("A", "B", "C", "default in case of error")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandlingWithOnErrorReturn() {
        final Flux<String> flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("error")))
                .concatWith(Flux.just("D")) // won't be called
                .onErrorReturn("default in case of error")
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("A", "B", "C", "default in case of error")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandlingWithOnErrorMap() {
        final Flux<String> flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("error")))
                .concatWith(Flux.just("D")) // won't be called
                .onErrorMap(IllegalArgumentException::new)
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandlingWithOnErrorMapWithRetry() {
        final Flux<String> flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("error")))
                .concatWith(Flux.just("D")) // won't be called
                .retry(2)
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandlingWithOnErrorMapWithRetryBackoff() {
        final Flux<String> flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("error")))
                .concatWith(Flux.just("D")) // won't be called
                .retryBackoff(2, Duration.ofSeconds(5))
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify();
    }
}
