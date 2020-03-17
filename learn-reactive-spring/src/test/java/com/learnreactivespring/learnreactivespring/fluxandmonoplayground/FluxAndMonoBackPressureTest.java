package com.learnreactivespring.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {

    @Test
    public void fluxWithBackPressure() {
        final Flux<Integer> flux = Flux.range(1, 10)
                .log();

        StepVerifier.create(flux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void fluxWithBackPressureWithRequest() {
        Flux.range(1, 10)
                .log()
                .subscribe(
                        element -> System.out.println("element is: " + element),
                        error -> System.err.println("error is: " + error),
                        () -> System.out.println("complete"),
                        subscription -> subscription.request(2)
                );
    }

    @Test
    public void fluxWithBackPressureWithCancel() {
        Flux.range(1, 10)
                .log()
                .subscribe(
                        element -> System.out.println("element is: " + element),
                        error -> System.err.println("error is: " + error),
                        () -> System.out.println("complete"),
                        Subscription::cancel
                );
    }

    @Test
    public void fluxWithCustomizedBackPressure() {
        Flux.range(1, 10)
                .log()
                .subscribe(new BaseSubscriber<Integer>() {

                    @Override
                    protected void hookOnNext(Integer value) {
                        request(1);
                        System.out.println("value received is: " + value);

                        if (value == 4) {
                            cancel();
                        }
                    }
                });
    }
}
