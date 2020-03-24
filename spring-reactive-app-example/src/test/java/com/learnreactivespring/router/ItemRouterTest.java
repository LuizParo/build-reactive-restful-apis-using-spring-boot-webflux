package com.learnreactivespring.router;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Comparator.comparing;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class ItemRouterTest {
    private static final String ITEMS_ENDPOINT = "/v1/functional/items";

    private static final Item ITEM_1 = new Item(UUID.randomUUID().toString(), "Apple Watch", 299.99);
    private static final Item ITEM_2 = new Item(UUID.randomUUID().toString(), "Beats Headphone", 149.99);
    private static final Item ITEM_3 = new Item(UUID.randomUUID().toString(), "Bose Headphone", 149.99);
    private static final Item ITEM_4 = new Item(UUID.randomUUID().toString(), "LG TV", 420.0);
    private static final Item ITEM_5 = new Item(UUID.randomUUID().toString(), "Samsung TV", 400.0);

    private static final List<Item> ITEMS = Arrays.asList(
            ITEM_1,
            ITEM_2,
            ITEM_3,
            ITEM_4,
            ITEM_5
    );

    @Autowired
    private WebTestClient testClient;

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @BeforeEach
    void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(ITEMS))
                .flatMap(itemReactiveRepository::save)
                .blockLast();
    }

    @Test
    void getAllItems() {
        testClient.get()
                .uri(ITEMS_ENDPOINT)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5)
                .contains(ITEM_1)
                .contains(ITEM_2)
                .contains(ITEM_3)
                .contains(ITEM_4)
                .contains(ITEM_5);
    }

    @Test
    void getAllItemsShouldNotReturnEmptyIds() {
        testClient.get()
                .uri(ITEMS_ENDPOINT)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBodyList(Item.class)
                .consumeWith(result -> assertThat(
                        result.getResponseBody(),
                        everyItem(hasProperty("id", not(emptyOrNullString())))
                ));
    }

    @Test
    void getAllItemsWithStepVerifier() {
        final Flux<Item> items = testClient.get()
                .uri(ITEMS_ENDPOINT)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(items.sort(comparing(Item::getDescription)))
                .expectSubscription()
                .expectNext(ITEM_1, ITEM_2, ITEM_3, ITEM_4, ITEM_5)
                .verifyComplete();
    }

    @Test
    void getById() {
        testClient.get()
                .uri(ITEMS_ENDPOINT + "/{id}", ITEM_2.getId())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody(Item.class)
                .isEqualTo(ITEM_2);

    }

    @Test
    void getByIdWithNotFound() {
        testClient.get()
                .uri(ITEMS_ENDPOINT + "/{id}", UUID.randomUUID().toString())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .isEmpty();
    }

    @Test
    void saveItem() {
        final Item newItem = new Item(null, "Iphone X", 999.99);

        testClient.post()
                .uri(ITEMS_ENDPOINT)
                .contentType(APPLICATION_JSON)
                .bodyValue(newItem)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueMatches("Location", ITEMS_ENDPOINT + "/[\\w]+")
                .expectBody(Item.class)
                .consumeWith(result -> assertItem(result.getResponseBody(), newItem));
    }

    @Test
    void deleteItemById() {
        testClient.delete()
                .uri(ITEMS_ENDPOINT + "/{id}", ITEM_3.getId())
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();

        testClient.get()
                .uri(ITEMS_ENDPOINT + "/{id}", ITEM_3.getId())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .isEmpty();
    }

    @Test
    void deleteItemByIdWithNotFound() {
        testClient.delete()
                .uri(ITEMS_ENDPOINT + "/{id}", UUID.randomUUID().toString())
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .isEmpty();
    }

    @Test
    void updateItem() {
        final Item itemToBeUpdated = new Item(ITEM_4.getId(), UUID.randomUUID().toString(), ThreadLocalRandom.current().nextDouble());

        testClient.put()
                .uri(ITEMS_ENDPOINT + "/{id}", ITEM_4.getId())
                .contentType(APPLICATION_JSON)
                .bodyValue(itemToBeUpdated)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(ITEM_4.getId())
                .jsonPath("$.description").isEqualTo(itemToBeUpdated.getDescription())
                .jsonPath("$.price").isEqualTo(itemToBeUpdated.getPrice());
    }

    @Test
    void updateItemWithNotFound() {
        final Item itemToBeUpdated = new Item(ITEM_4.getId(), UUID.randomUUID().toString(), ThreadLocalRandom.current().nextDouble());

        testClient.put()
                .uri(ITEMS_ENDPOINT + "/{id}", UUID.randomUUID().toString())
                .contentType(APPLICATION_JSON)
                .bodyValue(itemToBeUpdated)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .isEmpty();
    }

    @Test
    void getItemsWithException() {
        testClient.get()
                .uri(ITEMS_ENDPOINT + "/with-exception")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("an exception occurred");
    }

    private void assertItem(Item actual, Item expected) {
        assertThat(actual, allOf(
                hasProperty("id", not(emptyOrNullString())),
                hasProperty("description", equalTo(expected.getDescription())),
                hasProperty("price", equalTo(expected.getPrice()))
        ));
    }
}