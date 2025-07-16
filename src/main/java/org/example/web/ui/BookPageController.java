package org.example.web.ui;

import org.example.persistence.repo.BookRepository;
import org.example.web.exception.BookNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BookPageController {

  private final BookRepository bookRepository;

  public BookPageController(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @GetMapping("/books")
  public String booksPage(Model model) {
    model.addAttribute("books", bookRepository.findAll());
    return "books"; // Thymeleaf will look for books.html
  }

  @GetMapping("/books/{id}")
  public String bookDetailPage(@PathVariable long id, Model model) {
    var book =
        bookRepository
            .findById(id)
            .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));

    model.addAttribute("book", book);
    return "book-detail"; // Thymeleaf will look for book-detail.html
  }
}
