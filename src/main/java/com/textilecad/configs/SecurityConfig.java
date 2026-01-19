package com.textilecad.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.textilecad.jwt.JwtAuthenticationFilter;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final JwtAuthenticationFilter jwtAuthFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
      .csrf(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(auth -> auth
        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
        .requestMatchers("/", "/login", "/register", "/auth/**", "/error").permitAll()
        .requestMatchers("/css/**", "/js/**", "/materials/**").permitAll()
        .requestMatchers("/app", "/app/**").authenticated()
        .anyRequest().authenticated()
      )
      // обработка ошибок доступа (если бы были статические файлы)

      // .exceptionHandling(exception -> exception
      //     .authenticationEntryPoint((request, response, authException) -> {
      //       response.sendRedirect("/login?error=access_denied");
      //     })
      //   )

      // обработка ошибок доступа (для SPA-приложения)
      .exceptionHandling(exception -> exception
        .authenticationEntryPoint((request, response, authException) -> {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.setContentType("application/json");
          response.getWriter().write("{\"error\":\"Unauthorized\"}");
        })
      )
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless для JWT
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // Регистрация JWT фильтра
      .build();
  }

  
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
