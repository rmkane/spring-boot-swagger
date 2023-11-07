package org.example.service;

import java.util.List;
import org.example.persistence.model.Book;

public interface BookService {
  Iterable<Book> findAll();

  List<Book> findByTitle(String bookTitle);

  Book findOne(long id);

  Book create(Book book);

  void delete(long id);

  Book updateBook(Book book, long id);
}
