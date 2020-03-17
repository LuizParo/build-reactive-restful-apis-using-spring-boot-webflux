package com.learnreactivespring.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    public void fluxWithInfiniteSequence() throws InterruptedException {
        Flux.interval(Duration.ofMillis(100)) // starts from 0 --> infinite
            .subscribe(element -> System.out.println("value is: " + element));

        Thread.sleep(3000);
    }

    @Test
    public void fluxWithInfiniteSequenceAndStepVerifier() {
        final Flux<Long> flux = Flux.interval(Duration.ofMillis(100)) // starts from 0 --> infinite
                .take(3);

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void fluxWithInfiniteSequenceAndDelay() {
        final Flux<Long> flux = Flux.interval(Duration.ofMillis(100)) // starts from 0 --> infinite
                .delayElements(Duration.ofSeconds(1))
                .take(3)
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }
}
