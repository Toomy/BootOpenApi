package org.toomy.example.openapidemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.Charset;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

@Component
@RequiredArgsConstructor
@Log4j2
public class BooksLoader implements ApplicationRunner {

    @Value("classpath:books.json")
    private Resource resource;

    private final BookController controller;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        theLog.debug("Loading Books from resources");

        var booksJson = StreamUtils.copyToString(resource.getInputStream(), Charset.forName("UTF-8"));
        var books = objectMapper.readValue(booksJson, Book[].class);

        Stream.of(books).forEach(controller::addBook);
    }
}
