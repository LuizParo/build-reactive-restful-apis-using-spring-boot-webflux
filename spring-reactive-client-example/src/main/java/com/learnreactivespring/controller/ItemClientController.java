package com.learnreactivespring.controller;

import com.learnreactivespring.dto.Item;
import com.learnreactivespring.exceptions.HttpStatusNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.lang.String.format;
import static java.util.Locale.US;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RestController
@RequestMapping("/client")
public class ItemClientController {
    private static final String ITEMS_SERVER_ENDPOINT = "/v1/items";
    private final WebClient client;

    @Autowired
    public ItemClientController(WebClient client) {
        this.client = client;
    }

    @GetMapping("/with-retrieve")
    public Flux<Item> getAllItemsUsingRetrieve() {
        return client.get()
                .uri(ITEMS_SERVER_ENDPOINT)
                .retrieve()
                .bodyToFlux(Item.class);
    }

    @GetMapping("/with-exchange")
    public Flux<Item> getAllItemsUsingExchange() {
        return client.get()
                .uri(ITEMS_SERVER_ENDPOINT)
                .exchange()
                .flatMapMany(response -> response.bodyToFlux(Item.class));
    }

    @GetMapping("/{id}/with-retrieve")
    public Mono<Item> findByIdUsingRetrieve(@PathVariable("id") String id) {
        return client.get()
                .uri(ITEMS_SERVER_ENDPOINT + "/{id}", id)
                .accept(APPLICATION_JSON)
                .retrieve()
                .onStatus(
                        status -> status == NOT_FOUND,
                        response -> Mono.error(new HttpStatusNotFoundException(format(US, "item with id %s not found", id)))
                )
                .bodyToMono(Item.class);
    }

    @GetMapping("/{id}/with-exchange")
    public Mono<Item> findByUdUsingExchange(@PathVariable("id") String id) {
        return client.get()
                .uri(ITEMS_SERVER_ENDPOINT + "/{id}", id)
                .accept(APPLICATION_JSON)
                .exchange()
                .flatMap(response -> response.bodyToMono(Item.class));
    }

    @PostMapping
    public Mono<Item> saveItem(@RequestBody Item item) {
        return client.post()
                .uri(ITEMS_SERVER_ENDPOINT)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .retrieve()
                .bodyToMono(Item.class);
    }

    @PutMapping("/{id}")
    public Mono<Item> updateItem(@PathVariable("id") String id, @RequestBody Item item) {
        return client.put()
                .uri(ITEMS_SERVER_ENDPOINT + "/{id}", id)
                .contentType(APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .retrieve()
                .onStatus(
                        status -> status == NOT_FOUND,
                        response -> Mono.error(new HttpStatusNotFoundException(format(US, "item with id %s not found", id)))
                        )
                .bodyToMono(Item.class);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteItemById(@PathVariable("id") String id) {
        return client.delete()
                .uri(ITEMS_SERVER_ENDPOINT + "/{id}", id)
                .retrieve()
                .onStatus(
                        status -> status == NOT_FOUND,
                        response -> Mono.error(new HttpStatusNotFoundException(format(US, "item with id %s not found", id)))
                )
                .bodyToMono(Void.class);
    }

    @GetMapping("/error/with-retrieve")
    public Mono<Void> errorWithRetrieve() {
        return client.get()
                .uri(ITEMS_SERVER_ENDPOINT + "/with-exception")
                .retrieve()
                .onStatus(
                        HttpStatus::is5xxServerError,
                        response -> response.bodyToMono(String.class)
                                            .flatMap(message -> Mono.error(new RuntimeException(message)))
                )
                .bodyToMono(Void.class);
    }

    @GetMapping("/error/with-exchange")
    public Flux<Item> errorWithExchange() {
        return client.get()
                .uri(ITEMS_SERVER_ENDPOINT + "/with-exception")
                .exchange()
                .flatMapMany(
                        response -> response.statusCode().is5xxServerError()
                                ? response.bodyToMono(String.class).flatMap(message -> Mono.error(new RuntimeException(message)))
                                : response.bodyToFlux(Item.class)
                );
    }
}
