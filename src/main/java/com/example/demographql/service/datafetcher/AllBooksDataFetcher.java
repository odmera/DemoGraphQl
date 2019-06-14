package com.example.demographql.service.datafetcher;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demographql.model.Book;
import com.example.demographql.repository.BookRepository;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Component
public class AllBooksDataFetcher implements DataFetcher<List<Book>> {

    private BookRepository bookRepository;

    @Autowired
    public AllBooksDataFetcher(BookRepository bookRepository) {
        this.bookRepository=bookRepository;
    }

    @Override
    public List<Book> get(DataFetchingEnvironment dataFetchingEnvironment) {
        return bookRepository.findAll();
    }
}
