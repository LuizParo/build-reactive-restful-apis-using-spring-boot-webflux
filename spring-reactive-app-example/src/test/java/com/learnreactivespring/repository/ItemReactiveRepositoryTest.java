package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.Comparator.comparing;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.not;

@DataMongoTest
@DirtiesContext
@ExtendWith(SpringExtension.class)
class ItemReactiveRepositoryTest {
    private static final Item ITEM_1 = new Item(null, "Samsung TV", 400.0);
    private static final Item ITEM_2 = new Item(null, "LG TV", 420.0);
    private static final Item ITEM_3 = new Item(null, "Apple Watch", 299.99);
    private static final Item ITEM_4 = new Item(null, "Beats Headphone", 149.99);
    private static final Item ITEM_5 = new Item(UUID.randomUUID().toString(), "Bose Headphone", 149.99);

    private static final List<Item> ITEMS = Arrays.asList(
            ITEM_1,
            ITEM_2,
            ITEM_3,
            ITEM_4,
            ITEM_5
    );

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @BeforeEach
    void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(ITEMS))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Inserted item is: " + item))
                .blockLast();
    }

    @Test
    void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll().sort(comparing(Item::getDescription)))
                .expectSubscription()
                .consumeNextWith(item -> assertItem(item, ITEM_3))
                .consumeNextWith(item -> assertItem(item, ITEM_4))
                .consumeNextWith(item -> assertItem(item, ITEM_5))
                .consumeNextWith(item -> assertItem(item, ITEM_2))
                .consumeNextWith(item -> assertItem(item, ITEM_1))
                .verifyComplete();
    }

    @Test
    public void getItemById() {
        StepVerifier.create(itemReactiveRepository.findById(ITEM_5.getId()))
                .expectSubscription()
                .expectNext(ITEM_5)
                .verifyComplete();
    }

    @Test
    public void findByDescription() {
        StepVerifier.create(itemReactiveRepository.findByDescription(ITEM_1.getDescription()))
                .expectSubscription()
                .consumeNextWith(item -> assertItem(item, ITEM_1))
                .verifyComplete();
    }

    @Test
    public void saveItem() {
        final Item item = new Item(UUID.randomUUID().toString(), "Google Home Mini", 30.0);

        StepVerifier.create(itemReactiveRepository.save(item))
                .expectSubscription()
                .expectNext(item)
                .verifyComplete();
    }

    @Test
    public void updateItem() {
        final double newPrice = 520.00D;

        final Mono<Item> updatedItem = itemReactiveRepository.findByDescription(ITEM_2.getDescription())
                .map(item -> item.withPrice(newPrice))
                .flatMap(itemReactiveRepository::save);

        StepVerifier.create(updatedItem)
                .expectSubscription()
                .consumeNextWith(item -> assertItem(item, ITEM_2.withPrice(newPrice)))
                .verifyComplete();
    }

    @Test
    public void deleteById() {
        final String idToBeDeleted = ITEM_5.getId();

        final Flux<Item> remainingItems = itemReactiveRepository.findById(idToBeDeleted)
                .map(Item::getId)
                .flatMap(itemReactiveRepository::deleteById)
                .thenMany(itemReactiveRepository.findAll())
                .sort(comparing(Item::getDescription));

        StepVerifier.create(remainingItems)
                .expectSubscription()
                .consumeNextWith(item -> assertItem(item, ITEM_3))
                .consumeNextWith(item -> assertItem(item, ITEM_4))
                .consumeNextWith(item -> assertItem(item, ITEM_2))
                .consumeNextWith(item -> assertItem(item, ITEM_1))
                .verifyComplete();
    }

    @Test
    public void delete() {
        final String idToBeDeleted = ITEM_5.getId();

        final Flux<Item> remainingItems = itemReactiveRepository.findById(idToBeDeleted)
                .flatMap(itemReactiveRepository::delete)
                .thenMany(itemReactiveRepository.findAll())
                .sort(comparing(Item::getDescription));

        StepVerifier.create(remainingItems)
                .expectSubscription()
                .consumeNextWith(item -> assertItem(item, ITEM_3))
                .consumeNextWith(item -> assertItem(item, ITEM_4))
                .consumeNextWith(item -> assertItem(item, ITEM_2))
                .consumeNextWith(item -> assertItem(item, ITEM_1))
                .verifyComplete();
    }

    private void assertItem(Item actual, Item expected) {
        assertThat(actual, allOf(
                hasProperty("id", not(emptyOrNullString())),
                hasProperty("description", equalTo(expected.getDescription())),
                hasProperty("price", equalTo(expected.getPrice()))
        ));
    }
}