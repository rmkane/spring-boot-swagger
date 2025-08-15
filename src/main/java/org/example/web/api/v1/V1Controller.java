package org.example.web.api.v1;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "V1", description = "API v1")
public class V1Controller {
  @GetMapping
  public Map<String, Object> root() {
    return Map.of(
        "version", "1.0.0",
        "description", "Spring Boot API v1 endpoints",
        "endpoints",
            Map.of(
                "books",
                Map.of(
                    "GET", "/api/v1/books",
                    "POST", "/api/v1/books",
                    "PUT", "/api/v1/books/{id}",
                    "DELETE", "/api/v1/books/{id}")));
  }
}
