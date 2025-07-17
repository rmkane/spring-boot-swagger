package org.example.repository;

import java.util.List;
import org.example.repository.entity.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, Long> {
  List<Book> findByTitle(String title);
}
