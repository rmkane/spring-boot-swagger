package org.example.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.example.repository.entity.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

  @Mock() private BookService bookService;

  @Test
  void findAll() {
    Mockito.when(bookService.findAll()).thenReturn(Collections.emptyList());

    Iterable<Book> iterator = bookService.findAll();
    List<Book> actualList = new ArrayList<>();
    iterator.forEach(actualList::add);

    assertTrue(actualList.isEmpty());
  }
}
