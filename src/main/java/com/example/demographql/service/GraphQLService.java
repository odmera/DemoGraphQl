package com.example.demographql.service;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.example.demographql.model.Book;
import com.example.demographql.repository.BookRepository;
import com.example.demographql.service.datafetcher.AllBooksDataFetcher;
import com.example.demographql.service.datafetcher.BookDataFetcher;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@Service
public class GraphQLService {
    private static Logger logger = LoggerFactory.getLogger(GraphQLService.class);

    private BookRepository bookRepository;

    private AllBooksDataFetcher allBooksDataFetcher;

    private BookDataFetcher bookDataFetcher;

    @Value("classpath:books.graphql")
    Resource resource;

    private GraphQL graphQL;

    @Autowired
    public GraphQLService(BookRepository bookRepository, AllBooksDataFetcher allBooksDataFetcher,
                          BookDataFetcher bookDataFetcher) {
        this.bookRepository=bookRepository;
        this.allBooksDataFetcher=allBooksDataFetcher;
        this.bookDataFetcher=bookDataFetcher;
    }

    @PostConstruct
    private void loadSchema() throws IOException {
        logger.info("Entering loadSchema@GraphQLService");
        loadDataIntoHSQL();

        //Get the graphql file
        File file = resource.getFile();

        //Parse SchemaF
        TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(file);
        RuntimeWiring runtimeWiring = buildRuntimeWiring();
        GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    /**
     * se cargan los datos a la bd HSQL, tambien se podria hacer con un script (insert)
     */
    private void loadDataIntoHSQL() {
        Stream.of(
                new Book("1001", "The C Programming Language", "PHI Learning", "1978",
                        new String[] {
                                "Brian W. Kernighan (Contributor)",
                                "Dennis M. Ritchie"
                }),
                new Book("1002","Your Guide To Scrivener", "MakeUseOf.com", " April 21st 2013",
                        new String[] {
                                "Nicole Dionisio (Goodreads Author)"
                        }),
                new Book("1003","Beyond the Inbox: The Power User Guide to Gmail", " Kindle Edition", "November 19th 2012",
                        new String[] {
                                "Shay Shaked"
                                ,  "Justin Pot"
                                , "Angela Randall (Goodreads Author)"
                        }),
                new Book("1004","Scratch 2.0 Programming", "Smashwords Edition", "February 5th 2015",
                        new String[] {
                                "Denis Golikov (Goodreads Author)"
                        }),
                new Book("1005","Pro Git", "by Apress (first published 2009)", "2014",
                        new String[] {
                                "Scott Chacon"
                        })

        ).forEach(book -> {
            bookRepository.save(book);
        });
    }

    /**
     * estamos realizando un cableado en tiempo de ejecución con dos captadores de datos: todos los libros(allBooks) y libros(book)
     * Los nombres de todos los libros(allBooks) y libros(book) aquí definidos deben coincidir con los tipos definidos 
     * en el archivo GraphQL que ya hemos 
     * @return
     */
    private RuntimeWiring buildRuntimeWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type("Query", typeWiring -> typeWiring
                    .dataFetcher("allBooks", allBooksDataFetcher)
                    .dataFetcher("book", bookDataFetcher))
                    .build();
    }

    public GraphQL getGraphQL(){
        return graphQL;
    }
}
