package com.learnreactivespring.document;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Getter
@With
@EqualsAndHashCode
@ToString
@Document
public final class Item {
    private final String id;
    private final String description;
    private final Double price;

    public Item updateWith(Item newItem) {
        return withDescription(newItem.description)
                .withPrice(newItem.price);
    }
}
