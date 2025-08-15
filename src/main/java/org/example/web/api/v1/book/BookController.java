package org.example.web.api.v1.book;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.repository.entity.Book;
import org.example.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/books", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Books", description = "API for managing books")
@RequiredArgsConstructor
public class BookController {
  private final BookService bookService;

  @GetMapping
  public ResponseEntity<Iterable<Book>> findAll() {
    Iterable<Book> books = bookService.findAll();
    return ResponseEntity.ok(books); // 200 OK
  }

  @GetMapping("/title/{bookTitle}")
  public ResponseEntity<List<Book>> findByTitle(@PathVariable String bookTitle) {
    List<Book> books = bookService.findByTitle(bookTitle);
    return ResponseEntity.ok(books); // 200 OK
  }

  @GetMapping("/{id}")
  public ResponseEntity<Book> findOne(@PathVariable long id) {
    Book book = bookService.findOne(id);
    return ResponseEntity.ok(book); // 200 OK
  }

  @PostMapping
  public ResponseEntity<Book> create(@RequestBody Book book) {
    Book created = bookService.create(book);
    return ResponseEntity.status(HttpStatus.CREATED) // 201 Created
        .header("Location", "/api/v1/books/" + created.getId())
        .body(created);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable long id) {
    bookService.delete(id);
    return ResponseEntity.noContent().build(); // 204 No Content
  }

  @PutMapping("/{id}")
  public ResponseEntity<Book> updateBook(@RequestBody Book book, @PathVariable long id) {
    Book updated = bookService.updateBook(book, id);
    return ResponseEntity.ok(updated); // 200 OK
  }
}
