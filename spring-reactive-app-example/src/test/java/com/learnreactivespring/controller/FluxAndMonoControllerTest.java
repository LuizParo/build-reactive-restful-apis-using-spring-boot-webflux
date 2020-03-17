package com.learnreactivespring.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;

@ExtendWith(SpringExtension.class)
@WebFluxTest
class FluxAndMonoControllerTest {

    @Autowired
    private WebTestClient testClient;

    @Test
    void fluxEndpointWithStepVerifier() {
        final Flux<Integer> responseFlux = testClient.get()
                .uri("/flux")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(responseFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    void fluxEndpointWithWebTestClient() {
        testClient.get()
                .uri("/flux")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(Integer.class)
                .hasSize(4)
                .contains(1, 2, 3, 4);
    }

    @Test
    void fluxEndpointWithEntityExchangeResult() {
        final List<Integer> expectedResult = Arrays.asList(1, 2, 3, 4);

        final EntityExchangeResult<List<Integer>> result = testClient.get()
                .uri("/flux")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();

        assertThat(result.getResponseBody(), equalTo(expectedResult));
    }

    @Test
    void fluxEndpointWithEntityExchangeResultAndConsumeWith() {
        final List<Integer> expectedResult = Arrays.asList(1, 2, 3, 4);

        testClient.get()
                .uri("/flux")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith(result -> assertThat(result.getResponseBody(), equalTo(expectedResult)));
    }

    @Test
    void flexStreamEndpointWithInfiniteStream() {
        final Flux<Long> responseFlux = testClient.get()
                .uri("/fluxstream")
                .accept(APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(responseFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .thenCancel()
                .verify();
    }

    @Test
    void monoEndpoint() {
        testClient.get()
                .uri("/mono")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(result -> assertThat(result.getResponseBody(), equalTo(1)));
    }
}