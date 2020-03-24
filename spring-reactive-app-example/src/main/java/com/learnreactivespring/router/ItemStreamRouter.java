package com.learnreactivespring.router;

import com.learnreactivespring.handler.ItemStreamHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class ItemStreamRouter {
    private static final String ENDPOINT = "/v1/functional/stream/items";

    @Bean
    public RouterFunction<ServerResponse> itemStreamRoutes(ItemStreamHandler itemStreamHandler) {
        return RouterFunctions.route(
                GET(ENDPOINT).and(accept(APPLICATION_STREAM_JSON)),
                request -> itemStreamHandler.getAllItems()
        );
    }
}
