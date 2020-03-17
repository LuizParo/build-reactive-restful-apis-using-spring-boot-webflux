package com.learnreactivespring.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

    @Test
    public void fluxTest() {
        final Flux<String> flux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("After error"))
                .log();

        flux.subscribe(
                System.out::println,
                System.err::println,
                () -> System.out.println("completed")
        );
    }

    @Test
    public void fluxTestWithoutError() {
        final Flux<String> flux = Flux.just("Spring", "Spring Boot", "Reactive Spring");

        StepVerifier.create(flux)
            .expectNext("Spring")
            .expectNext("Spring Boot")
            .expectNext("Reactive Spring")
            .verifyComplete();
    }

    @Test
    public void fluxTestWithError() {
        final Flux<String> flux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        StepVerifier.create(flux)
            .expectNext("Spring", "Spring Boot", "Reactive Spring")
//            .expectError(RuntimeException.class)
            .expectErrorMessage("Exception Occurred")
            .verify();
    }

    @Test
    public void fluxTestCountWithError() {
        final Flux<String> flux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        StepVerifier.create(flux)
            .expectNextCount(3)
            .expectErrorMessage("Exception Occurred")
            .verify();
    }

    @Test
    public void monoTest() {
        final Mono<String> mono = Mono.just("Spring");

        StepVerifier.create(mono)
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void monoTestWithError() {
        StepVerifier.create(Mono.error(new RuntimeException("exception")).log())
                .expectError(RuntimeException.class)
                .verify();
    }
}
