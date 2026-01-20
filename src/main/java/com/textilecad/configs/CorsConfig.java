package com.textilecad.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer{
@Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOrigins("https://localhost:5173") // Порт Vue
      .allowedMethods("GET", "POST", "PUT", "DELETE","OPTIONS")
      .allowedHeaders("*")
      .allowCredentials(true) // Разрешить передачу Cookies
      .maxAge(3600);
  }
}
