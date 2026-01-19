package com.textilecad.controllers;
import java.util.Map;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.textilecad.jwt.JwtService;
import com.textilecad.models.User;
import com.textilecad.services.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserControler {
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  @PostMapping("/register")
  public String addUser(@RequestBody User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setActive(true);
    userService.addUser(user);
    return "Пользователь зарегистрирован";
  }

  @PostMapping("/login")
  public String login(@RequestBody User loginRequest, HttpServletResponse response) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
    );
    final UserDetails user = userDetailsService.loadUserByUsername(loginRequest.getEmail());

    String jwt_token = jwtService.generateToken(user);

    ResponseCookie cookie = ResponseCookie.from("jwt_token", jwt_token)
      .httpOnly(true)
      .secure(false) // ставьте true в production на HTTPS
      .path("/")
      .maxAge(24 * 60 * 60)
      .sameSite("Lax") // для кросс-сайтового обмена в prod — "None" + secure=true
      .build();

    response.addHeader("Set-Cookie", cookie.toString());



    return "Успешный вход";
  }

  @GetMapping("/me")
  public ResponseEntity<?> me(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
    }
    String email = authentication.getName();
    return ResponseEntity.ok(Map.of("email", email));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletResponse response) {
      ResponseCookie cookie = ResponseCookie.from("jwt_token", "")
          .httpOnly(true)
          .secure(false)
          .path("/")
          .maxAge(0)
          .sameSite("Lax")
          .build();
      response.addHeader("Set-Cookie", cookie.toString());
      return ResponseEntity.ok(Map.of("message", "Logged out"));
  }

}
