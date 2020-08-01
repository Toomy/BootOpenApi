package org.toomy.example.openapidemo;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Book {

    String isbn;
    String name;
    String author;
}
