package com.learnreactivespring.initializer;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemCappedReactiveRepository;
import com.learnreactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Profile("!test")
@Component
public class ItemDataInitializer implements CommandLineRunner {
    private final ItemReactiveRepository itemReactiveRepository;
    private final ItemCappedReactiveRepository itemCappedReactiveRepository;
    private final MongoOperations mongoOperations;

    @Autowired
    public ItemDataInitializer(ItemReactiveRepository itemReactiveRepository,
                               ItemCappedReactiveRepository itemCappedReactiveRepository,
                               MongoOperations mongoOperations) {
        this.itemReactiveRepository = itemReactiveRepository;
        this.itemCappedReactiveRepository = itemCappedReactiveRepository;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public void run(String... args) {
            initialDataSetup();
            createCappedCollection();
            initialDataSetupForCappedCollection();
    }

    private void initialDataSetup() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items()))
                .flatMap(itemReactiveRepository::save)
                .subscribe(item -> log.info("created item: " + item));
    }

    private void createCappedCollection() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(
                ItemCapped.class,
                CollectionOptions.empty()
                                 .maxDocuments(20)
                                 .size(50_000)
                                 .capped()
        );
    }

    private void initialDataSetupForCappedCollection() {
        Flux.interval(Duration.ofSeconds(1))
            .map(i -> new ItemCapped(null, "random item " + i, 100D + i))
            .flatMap(itemCappedReactiveRepository::save)
            .subscribe(item -> log.info("created capped item: " + item));
    }

    private List<Item> items() {
        return Arrays.asList(
                new Item(null, "Samsung TV", 399.99),
                new Item(null, "LG TV", 329.99),
                new Item(null, "Apple Watch", 349.99),
                new Item("ABC", "Beats HeadPhones", 149.99)
        );
    }
}
