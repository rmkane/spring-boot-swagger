package org.example.web.ui;

import org.example.persistence.repo.BookRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
