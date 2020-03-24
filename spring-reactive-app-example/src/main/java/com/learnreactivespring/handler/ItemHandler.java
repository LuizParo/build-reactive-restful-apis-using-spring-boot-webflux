package com.learnreactivespring.handler;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static java.lang.String.format;
import static java.util.Locale.US;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class ItemHandler {
    private final ItemReactiveRepository itemReactiveRepository;

    @Autowired
    public ItemHandler(ItemReactiveRepository itemReactiveRepository) {
        this.itemReactiveRepository = itemReactiveRepository;
    }

    public Mono<ServerResponse> getAllItems() {
        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getItemById(ServerRequest request) {
        return itemReactiveRepository.findById(request.pathVariable("id"))
                .flatMap(item -> ServerResponse.ok()
                                               .contentType(APPLICATION_JSON)
                                               .body(Mono.just(item), Item.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> saveItem(ServerRequest request) {
        return request.bodyToMono(Item.class)
                .flatMap(itemReactiveRepository::save)
                .flatMap(savedItem -> ServerResponse.created(URI.create(format(US, "%s/%s", request.path(), savedItem.getId())))
                                                    .contentType(APPLICATION_JSON)
                                                    .body(Mono.just(savedItem), Item.class));
    }

    public Mono<ServerResponse> deleteItemById(ServerRequest request) {
        return itemReactiveRepository.deleteItemById(request.pathVariable("id"))
                .filter(removedDocuments -> removedDocuments > 0)
                .flatMap(removedDocuments ->  ServerResponse.noContent().build())
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> updateItem(ServerRequest request) {
        return itemReactiveRepository.findById(request.pathVariable("id"))
                .flatMap(item -> request.bodyToMono(Item.class)
                                        .map(item::updateWith))
                .flatMap(itemReactiveRepository::save)
                .flatMap(updatedItem -> ServerResponse.ok().body(Mono.just(updatedItem), Item.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getItemsWithException() {
        throw new RuntimeException("an exception occurred");
    }
}
