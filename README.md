<!-- omit in toc -->
# Spring Boot Swagger Example

A modern Spring Boot 3 application demonstrating:

- REST API with OpenAPI 3 (Swagger UI)
- H2 in-memory database with Flyway migrations
- Thymeleaf UI
- Spring Security
- Theming and global error handling
- Production-ready monitoring with Spring Boot Actuator

<!-- omit in toc -->
## Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Features](#features)
- [Endpoints](#endpoints)
- [References](#references)

## Overview

This project is a template for building robust, maintainable Spring Boot applications with a focus on:

- Clean architecture and package structure
- API documentation and discoverability
- Developer experience and maintainability

## Prerequisites

- Java 21+
- Maven 3.8+

## Getting Started

1. **Build the project:**

   ```sh
   make
   ```

2. **Run the application:**

   ```sh
   make run
   ```

3. **Access the app:**
   - UI: [http://localhost:42069/](http://localhost:42069/)
   - Swagger UI: [http://localhost:42069/swagger-ui/index.html](http://localhost:42069/swagger-ui/index.html)
   - H2 Console: [http://localhost:42069/h2](http://localhost:42069/h2)
   - Actuator: [http://localhost:42069/actuator/health](http://localhost:42069/actuator/health)

## Features

- **REST API**: CRUD for books, documented with OpenAPI/Swagger
- **Themed UI**: Modern, responsive, accessible
- **H2 Database**: In-memory, with Flyway migrations
- **Security**: Basic Spring Security setup
- **Error Handling**: Custom error pages and JSON responses
- **Monitoring**: Spring Boot Actuator endpoints
- **Developer Experience**: Makefile, code formatting, tests

## Endpoints

| Feature         | URL                                             | Description          |
|-----------------|-------------------------------------------------|--------------------- |
| App UI          | [/](/)                                          | Main web interface   |
| Swagger UI      | [/swagger-ui/index.html](swagger-ui/index.html) | API docs & testing   |
| H2 Console      | [/h2](h2)                                       | In-memory DB UI      |
| API Root        | [/api/books](api/books)                         | Book REST API        |
| Actuator Health | [/actuator/health](actuator/health)             | Health check         |
| Actuator Info   | [/actuator/info](actuator/info)                 | App info             |
| Actuator Metrics| [/actuator/metrics](actuator/metrics)           | Metrics & monitoring |

## References

- [Spring Boot Getting Started](https://www.baeldung.com/spring-boot-start)
- [Spring Boot H2 Database](https://www.baeldung.com/spring-boot-h2-database)
- [Spring Boot Log4j2](https://www.baeldung.com/spring-boot-logback-log4j2)
- [Spring REST OpenAPI](https://www.baeldung.com/spring-rest-openapi-documentation)
- [Spring Boot Actuator Docs](https://docs.spring.io/spring-boot/docs/current/actuator-api/htmlsingle/)
