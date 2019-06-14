package com.example.demographql.service.datafetcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demographql.model.Book;
import com.example.demographql.repository.BookRepository;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Component
public class BookDataFetcher implements DataFetcher<Book> {

    private BookRepository bookRepository;

    @Autowired
    public BookDataFetcher(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    @Override
    public Book get(DataFetchingEnvironment dataFetchingEnvironment) {
        String isn = dataFetchingEnvironment.getArgument("id");
        return bookRepository.findById(isn).orElse(null);
    }
}