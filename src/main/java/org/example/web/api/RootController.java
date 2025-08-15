package org.example.web.api;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Root", description = "API root")
public class RootController {
  @GetMapping
  public Map<String, Object> root() {
    return Map.of(
        "name", "Spring Boot API",
        "description", "API for Spring Boot application",
        "versions",
            Map.of(
                "v1",
                Map.of(
                    "status", "current",
                    "url", "/api/v1")),
        "latestVersion", "v1",
        "defaultVersion", "v1");
  }
}
