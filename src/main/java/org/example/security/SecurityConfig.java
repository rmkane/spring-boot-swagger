package org.example.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors(withDefaults())
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/h2/**")
                    .permitAll() // H2 console
                    .requestMatchers("/api/**")
                    .permitAll() // your REST API (adjust path if needed)
                    .anyRequest()
                    .permitAll() // allow everything else too
            )
        .headers(
            headers ->
                headers.frameOptions(frameOptions -> frameOptions.sameOrigin()) // allow H2 iframes
            )
        .csrf(
            csrf -> csrf.ignoringRequestMatchers("/h2/**", "/api/**") // disable CSRF for H2 + API
            );

    return http.build();
  }

  @Bean
  public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOriginPatterns(List.of("*")); // dev-only: allow all
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return new CorsFilter(source);
  }
}
