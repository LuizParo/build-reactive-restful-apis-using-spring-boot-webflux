package com.learnreactivespring.router;

import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemCappedReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.Comparator.comparing;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;

@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class ItemStreamRouterTest {
    private static final String STREAM_ITEMS_ENDPOINT = "/v1/functional/stream/items";

    private static final ItemCapped ITEM_1 = new ItemCapped(UUID.randomUUID().toString(), "Apple Watch", 299.99);
    private static final ItemCapped ITEM_2 = new ItemCapped(UUID.randomUUID().toString(), "Beats Headphone", 149.99);
    private static final ItemCapped ITEM_3 = new ItemCapped(UUID.randomUUID().toString(), "Bose Headphone", 149.99);
    private static final ItemCapped ITEM_4 = new ItemCapped(UUID.randomUUID().toString(), "LG TV", 420.0);
    private static final ItemCapped ITEM_5 = new ItemCapped(UUID.randomUUID().toString(), "Samsung TV", 400.0);

    private static final List<ItemCapped> ITEMS = Arrays.asList(
            ITEM_1,
            ITEM_2,
            ITEM_3,
            ITEM_4,
            ITEM_5
    );

    @Autowired
    private WebTestClient testClient;

    @Autowired
    private ItemCappedReactiveRepository itemCappedReactiveRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @BeforeEach
    void setUp() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(
                ItemCapped.class,
                CollectionOptions.empty()
                        .maxDocuments(20)
                        .size(50_000)
                        .capped()
        );

        Flux.interval(Duration.ofMillis(100))
                .map(Long::intValue)
                .map(ITEMS::get)
                .take(ITEMS.size())
                .flatMap(itemCappedReactiveRepository::save)
                .doOnNext(item -> System.out.println("created capped item: " + item))
                .blockLast();
    }

    @Test
    void getItems() {
        final Flux<ItemCapped> items = testClient.get()
                .uri(STREAM_ITEMS_ENDPOINT)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_STREAM_JSON)
                .returnResult(ItemCapped.class)
                .getResponseBody()
                .take(ITEMS.size());

        StepVerifier.create(items.sort(comparing(ItemCapped::getDescription)))
                .expectSubscription()
                .expectNext(ITEM_1)
                .expectNext(ITEM_2)
                .expectNext(ITEM_3)
                .expectNext(ITEM_4)
                .expectNext(ITEM_5)
                .verifyComplete();
    }
}