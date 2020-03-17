package com.learnreactivespring.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdAndHotPublisherTest {

    @Test
    public void coldPublisherTest() throws InterruptedException {
        final Flux<String> flux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        flux.subscribe(element -> System.out.println("Subscriber 1: " + element)); // emits values from the beginning

        Thread.sleep(2000);

        flux.subscribe(element -> System.out.println("Subscriber 2: " + element)); // emits values from the beginning

        Thread.sleep(4000);
    }


    @Test
    public void hotPublisherTest() throws InterruptedException {
        final Flux<String> flux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        final ConnectableFlux<String> connectableFlux = flux.publish();
        connectableFlux.connect();

        connectableFlux.subscribe(element -> System.out.println("Subscriber 1: " + element));

        Thread.sleep(3000);

        connectableFlux.subscribe(element -> System.out.println("Subscriber 2: " + element)); // does not emit values from beginning

        Thread.sleep(4000);
    }
}
