package org.example.service.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.persistence.model.Book;
import org.example.persistence.repo.BookRepository;
import org.example.service.BookService;
import org.example.web.exception.BookIdMismatchException;
import org.example.web.exception.BookNotFoundException;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class BookServiceImpl implements BookService {
  private BookRepository bookRepository;

  @Override
  public Iterable<Book> findAll() {
    log.warn("Finding all books...");
    return bookRepository.findAll();
  }

  @Override
  public List<Book> findByTitle(String bookTitle) {
    return bookRepository.findByTitle(bookTitle);
  }

  @Override
  public Book findOne(long id) {
    return bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
  }

  @Override
  public Book create(Book book) {
    return bookRepository.save(book);
  }

  @Override
  public void delete(long id) {
    bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
    bookRepository.deleteById(id);
  }

  @Override
  public Book updateBook(Book book, long id) {
    if (book.getId() != id) {
      throw new BookIdMismatchException();
    }
    bookRepository.findById(id).orElseThrow(BookNotFoundException::new);
    return bookRepository.save(book);
  }
}
