package db.migration;

import java.util.LinkedHashMap;
import java.util.Map;

public class V1__Setup extends FlywayMigration {

  @Override
  protected Map<String, String> getMigrations() {
    Map<String, String> migrations = new LinkedHashMap<>();

    migrations.put(
        "create_book_table",
        """
            CREATE TABLE book (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255),
                author VARCHAR(255)
            );
        """);

    migrations.put(
        "insert_sample_books",
        """
            INSERT INTO book (title, author) VALUES
            ('The Hobbit', 'J.R.R. Tolkien'),
            ('1984', 'George Orwell'),
            ('Fahrenheit 451', 'Ray Bradbury');
        """);

    return migrations;
  }
}
