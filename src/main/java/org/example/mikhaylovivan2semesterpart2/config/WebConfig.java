package org.example.mikhaylovivan2semesterpart2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

@Configuration
public class WebConfig implements WebFluxConfigurer {

  @Bean
  public WebFilter userIdFilter() {
    return (ServerWebExchange exchange, WebFilterChain chain) -> {
      String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
      if (userId != null) {
        exchange.getAttributes().put("userId", userId);
      }
      return chain.filter(exchange);
    };
  }
}
