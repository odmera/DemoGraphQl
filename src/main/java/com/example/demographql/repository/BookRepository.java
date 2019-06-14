package com.example.demographql.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demographql.model.Book;

public interface BookRepository extends JpaRepository<Book, String>
{
}