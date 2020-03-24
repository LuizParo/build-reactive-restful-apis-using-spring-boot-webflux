package com.learnreactivespring.controller.v1;

import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemCappedReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON_VALUE;

@RestController
@RequestMapping("/v1/stream/items")
public class ItemStreamController {
    private final ItemCappedReactiveRepository itemCappedReactiveRepository;

    @Autowired
    public ItemStreamController(ItemCappedReactiveRepository itemCappedReactiveRepository) {
        this.itemCappedReactiveRepository = itemCappedReactiveRepository;
    }

    @GetMapping(produces = APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getItems() {
        return itemCappedReactiveRepository.findItemsBy();
    }
}
