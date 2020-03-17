package com.learnreactivespring.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static java.util.function.Function.identity;
import static reactor.core.scheduler.Schedulers.parallel;

public class FlexAndMonoTransformTest {
    private static final List<String> NAMES = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void fluxUsingMap() {
        final Flux<String> flux = Flux.fromIterable(NAMES)
                                      .map(String::toUpperCase)
                                      .log();

        StepVerifier.create(flux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                .verifyComplete();
    }

    @Test
    public void fluxUsingMapWithRepeat() {
        final Flux<Integer> flux = Flux.fromIterable(NAMES)
                                        .map(String::length)
                                        .repeat(1)
                                        .log();

        StepVerifier.create(flux)
                .expectNext(4, 4, 4, 5, 4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void fluxUsingFlatMap() {
        final Flux<String> flux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .map(this::convertToList)
                .flatMap(Flux::fromIterable)
                .log();

        StepVerifier.create(flux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void fluxUsingFlatMapWithParallel() {
        final Flux<String> flux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
                .map(flexOfStrings -> flexOfStrings.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(identity())
                .flatMap(Flux::fromIterable)
                .log();

        StepVerifier.create(flux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void fluxUsingFlatMapWithParallelMaintainingOrder() {
        final Flux<String> flux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
                .flatMapSequential(flexOfStrings -> flexOfStrings.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(Flux::fromIterable)
                .log();

        StepVerifier.create(flux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String string) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Arrays.asList(string, "newValue");
    }
}
