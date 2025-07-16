package org.example.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class CustomErrorPageResolver implements ErrorPageRegistrar {

  @Override
  public void registerErrorPages(ErrorPageRegistry registry) {
    // Register custom error pages for all error status codes
    ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/error");
    ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error");
    ErrorPage error400Page = new ErrorPage(HttpStatus.BAD_REQUEST, "/error");
    ErrorPage error403Page = new ErrorPage(HttpStatus.FORBIDDEN, "/error");
    ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/error");

    registry.addErrorPages(error404Page, error500Page, error400Page, error403Page, error401Page);
  }
}
