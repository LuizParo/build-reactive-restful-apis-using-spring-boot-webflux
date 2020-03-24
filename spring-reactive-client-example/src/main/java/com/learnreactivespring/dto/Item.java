package com.learnreactivespring.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class Item {
    private final String id;
    private final String description;
    private final Double price;

    @JsonCreator
    public Item(@JsonProperty("id") String id,
                @JsonProperty("description") String description,
                @JsonProperty("price") Double price) {
        this.id = id;
        this.description = description;
        this.price = price;
    }
}
