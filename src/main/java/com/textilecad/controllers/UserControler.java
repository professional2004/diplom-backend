package com.textilecad.controllers;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserControler {
  private final UserService userService;
  private final UserDetailsService userDetailsService;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

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
      .secure(true)
      .sameSite("None")
      .path("/")
      .maxAge(24 * 60 * 60)
      .build();

    response.addHeader("Set-Cookie", cookie.toString());
    return "Успешный вход";
  }
  

  @GetMapping("/check")
  public ResponseEntity<?> me(@AuthenticationPrincipal UserDetails user) {
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    return ResponseEntity.ok(Map.of("email", user.getUsername()));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletResponse response) {
      ResponseCookie cookie = ResponseCookie.from("jwt_token", "")
          .httpOnly(true)
          .secure(true)
          .sameSite("None")
          .path("/")
          .maxAge(0)
          .build();
      response.addHeader("Set-Cookie", cookie.toString());
      return ResponseEntity.ok(Map.of("message", "Logged out"));
  }

}
