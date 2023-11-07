package org.example.persistence.repo;

import java.util.List;
import org.example.persistence.model.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, Long> {
  List<Book> findByTitle(String title);
}
