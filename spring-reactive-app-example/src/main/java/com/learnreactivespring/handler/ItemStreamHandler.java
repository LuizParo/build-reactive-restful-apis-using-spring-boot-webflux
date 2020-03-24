package com.learnreactivespring.handler;

import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemCappedReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;

@Component
public class ItemStreamHandler {
    private final ItemCappedReactiveRepository itemCappedReactiveRepository;

    @Autowired
    public ItemStreamHandler(ItemCappedReactiveRepository itemCappedReactiveRepository) {
        this.itemCappedReactiveRepository = itemCappedReactiveRepository;
    }

    public Mono<ServerResponse> getAllItems() {
        return ServerResponse.ok()
                .contentType(APPLICATION_STREAM_JSON)
                .body(itemCappedReactiveRepository.findItemsBy(), ItemCapped.class);
    }
}
