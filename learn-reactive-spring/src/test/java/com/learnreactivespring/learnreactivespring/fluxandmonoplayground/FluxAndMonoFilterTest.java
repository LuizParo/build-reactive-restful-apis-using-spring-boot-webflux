package com.learnreactivespring.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {
    private static final List<String> NAMES = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void fluxUsingFilter() {
        final Flux<String> flux = Flux.fromIterable(NAMES)
                                      .filter(name -> name.startsWith("a"))
                                      .log();

        StepVerifier.create(flux)
                .expectNext("adam", "anna")
                .verifyComplete();
    }
}
