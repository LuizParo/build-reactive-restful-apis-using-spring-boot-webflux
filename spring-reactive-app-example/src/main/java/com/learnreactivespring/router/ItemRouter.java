package com.learnreactivespring.router;

import com.learnreactivespring.handler.ItemHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class ItemRouter {
    private static final String ENDPOINT = "/v1/functional/items";

    @Bean
    public RouterFunction<ServerResponse> itemErrorRouter(ItemHandler itemHandler) {
        return RouterFunctions.route(
                GET(ENDPOINT + "/with-exception").and(accept(APPLICATION_JSON)),
                request -> itemHandler.getItemsWithException()
        );
    }

    @Bean
    public RouterFunction<ServerResponse> itemRoutes(ItemHandler itemHandler) {
        return RouterFunctions.route(
                GET(ENDPOINT).and(accept(APPLICATION_JSON)),
                request -> itemHandler.getAllItems()
        )
        .andRoute(
                GET(ENDPOINT + "/{id}").and(accept(APPLICATION_JSON)),
                itemHandler::getItemById
        )
        .andRoute(
                POST(ENDPOINT).and(accept(APPLICATION_JSON)),
                itemHandler::saveItem
        )
        .andRoute(
                DELETE(ENDPOINT + "/{id}"),
                itemHandler::deleteItemById
        )
        .andRoute(
                PUT(ENDPOINT + "/{id}").and(accept(APPLICATION_JSON)),
                itemHandler::updateItem
        );
    }
}
