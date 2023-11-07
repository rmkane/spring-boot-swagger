package org.example.web;

import java.util.List;
import lombok.AllArgsConstructor;
import org.example.persistence.model.Book;
import org.example.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {
  private BookService bookService;

  @GetMapping
  public Iterable<Book> findAll() {
    return bookService.findAll();
  }

  @GetMapping("/title/{bookTitle}")
  public List<Book> findByTitle(@PathVariable String bookTitle) {
    return bookService.findByTitle(bookTitle);
  }

  @GetMapping("/{id}")
  public Book findOne(@PathVariable long id) {
    return bookService.findOne(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Book create(@RequestBody Book book) {
    return bookService.create(book);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable long id) {
    bookService.delete(id);
  }

  @PutMapping("/{id}")
  public Book updateBook(@RequestBody Book book, @PathVariable long id) {
    return bookService.updateBook(book, id);
  }
}
