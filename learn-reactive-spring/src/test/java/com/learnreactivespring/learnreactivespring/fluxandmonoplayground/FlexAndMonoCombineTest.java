package com.learnreactivespring.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FlexAndMonoCombineTest {

    @Test
    public void fluxWithCombine() {
        final Flux<String> flux1 = Flux.just("A", "B", "C");
        final Flux<String> flux2 = Flux.just("D", "E", "F");

        final Flux<String> mergedFlux = Flux.merge(flux1, flux2).log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void fluxWithCombineAndDelay() {
        final Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        final Flux<String> flux2 = Flux.just("D", "E", "F");

        final Flux<String> mergedFlux = Flux.merge(flux1, flux2).log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNextCount(6) // out of order due to the delay
                .verifyComplete();
    }

    @Test
    public void fluxWithConcat() {
        final Flux<String> flux1 = Flux.just("A", "B", "C");
        final Flux<String> flux2 = Flux.just("D", "E", "F");

        final Flux<String> mergedFlux = Flux.concat(flux1, flux2).log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void fluxWithConcatWithDelay() {
        final Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        final Flux<String> flux2 = Flux.just("D", "E", "F");

        final Flux<String> mergedFlux = Flux.concat(flux1, flux2).log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void fluxWithZip() {
        final Flux<String> flux1 = Flux.just("A", "B", "C");
        final Flux<String> flux2 = Flux.just("D", "E", "F");

        final Flux<String> mergedFlux = Flux.zip(flux1, flux2, String::concat).log(); // [AD] [BE] [CF]

        StepVerifier.create(mergedFlux)
                .expectSubscription()
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }
}
