package com.learnreactivespring.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class VirtualTimeTest {

    @Test
    public void flexWithoutVirtualTime() {
        final Flux<Long> flux = Flux.interval(Duration.ofSeconds(1))
                .take(3)
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void flexWithVirtualTime() {
        VirtualTimeScheduler.getOrSet();

        final Flux<Long> flux = Flux.interval(Duration.ofSeconds(1))
                .take(3)
                .log();

        StepVerifier.withVirtualTime(() -> flux)
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }
}
