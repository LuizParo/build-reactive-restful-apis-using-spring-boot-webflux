package com.learnreactivespring.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FlexAndMonoFactoryTest {
    private static final List<String> NAMES = Arrays.asList("adam", "anna", "jack", "jenny");
    private static final String[] NAMES_AS_ARRAY = { "adam", "anna", "jack", "jenny" };

    @Test
    public void fluxUsingIterable() {
        final Flux<String> flux = Flux.fromIterable(NAMES).log();

        StepVerifier.create(flux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray() {
        final Flux<String> flux = Flux.fromArray(NAMES_AS_ARRAY).log();

        StepVerifier.create(flux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingStream() {
        final Flux<String> flux = Flux.fromStream(NAMES.stream()).log();

        StepVerifier.create(flux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange() {
        final Flux<Integer> flux = Flux.range(1, 5).log();

        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    public void monoUsingJustOrEmptyWithNullValue() {
        final Mono<?> mono = Mono.justOrEmpty(null).log();

        StepVerifier.create(mono)
                    .verifyComplete();
    }

    @Test
    public void monoUsingJustOrEmptyWithAValue() {
        final Mono<String> mono = Mono.justOrEmpty("spring").log();

        StepVerifier.create(mono)
                    .expectNext("spring")
                    .verifyComplete();
    }

    @Test
    public void monoUsingSupplier() {
        final Mono<String> mono = Mono.fromSupplier(() -> "spring").log();

        StepVerifier.create(mono)
                    .expectNext("spring")
                    .verifyComplete();
    }
}
