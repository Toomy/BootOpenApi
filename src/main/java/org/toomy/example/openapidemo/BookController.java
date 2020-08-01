package org.toomy.example.openapidemo;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * https://www.baeldung.com/spring-rest-openapi-documentation
 */
@RestController
@RequestMapping("/api/book")
public class BookController {

    private final ConcurrentHashMap<String, Book> books = new ConcurrentHashMap<>();

    void addBook(Book book) {
        books.put(book.getIsbn(), book);
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<Book> findById(@PathVariable String isbn) {

        return Optional.ofNullable(books.get(isbn))
            .map(book -> ResponseEntity.ok(book))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Book> getAll() {
        return List.copyOf(books.values());
    }

    @PutMapping
    public ResponseEntity<Book> updateBook(@RequestBody Book book) {

        final AtomicBoolean updated = new AtomicBoolean(false);
        var retval = books.computeIfPresent(book.getIsbn(), (key, old) -> {
            updated.set(true);
            return book;
        });

        if (updated.get()) {
            return ResponseEntity.ok(retval);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book, UriComponentsBuilder ucb) {

        final AtomicBoolean created = new AtomicBoolean(false);
        var retval = books.computeIfAbsent(book.getIsbn(), key -> {
            created.set(true);
            return book;
        });

        if (created.get()) {
            return ResponseEntity.created(
                ucb.path("/api/book/{isbn}").buildAndExpand(book.getIsbn()).toUri())
                .body(book);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
