package com.textilecad.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.textilecad.dto.MessageResponseDTO;
import com.textilecad.dto.auth.LoginRequestDTO;
import com.textilecad.dto.auth.MeResponseDTO;
import com.textilecad.dto.auth.RegisterRequestDTO;
import com.textilecad.dto.auth.RegisterResponseDTO;
import com.textilecad.jwt.JwtService;
import com.textilecad.models.User;
import com.textilecad.services.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final UserDetailsService userDetailsService;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  

  @PostMapping("/register")
  public ResponseEntity<RegisterResponseDTO> addUser(@RequestBody RegisterRequestDTO request) {
    User user = new User();
    user.setEmail(request.email());
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setActive(true);

    User savedUser = userService.addUser(user);
    RegisterResponseDTO response = new RegisterResponseDTO(savedUser.getId(), savedUser.getEmail(), savedUser.isActive());
    return ResponseEntity.status(HttpStatus.CREATED).body(response); 
  }


  @PostMapping("/login")
  public ResponseEntity<MessageResponseDTO> login(@RequestBody LoginRequestDTO loginRequest, HttpServletResponse httpResponse) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
    );
    final UserDetails user = userDetailsService.loadUserByUsername(loginRequest.email());

    String jwt_token = jwtService.generateToken(user);

    ResponseCookie cookie = ResponseCookie.from("jwt_token", jwt_token)
      .httpOnly(true)
      .secure(true)
      .sameSite("None")
      .path("/")
      .maxAge(24 * 60 * 60)
      .build();

    httpResponse.addHeader("Set-Cookie", cookie.toString());
    MessageResponseDTO response = new MessageResponseDTO("Успешный вход");
    return ResponseEntity.status(HttpStatus.OK).body(response); 
  }


  @GetMapping("/me")
  public ResponseEntity<MeResponseDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    User user = userService.findByEmail(userDetails.getUsername());
    MeResponseDTO response = new MeResponseDTO(user.getId(), user.getEmail(), user.isActive());
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }


  @PostMapping("/logout")
  public ResponseEntity<MessageResponseDTO> logout(HttpServletResponse httpResponse) {
    ResponseCookie cookie = ResponseCookie.from("jwt_token", "")
      .httpOnly(true)
      .secure(true)
      .sameSite("None")
      .path("/")
      .maxAge(0)
      .build();
    httpResponse.addHeader("Set-Cookie", cookie.toString());

    MessageResponseDTO response = new MessageResponseDTO("Успешный выход из аккаунта");
    return ResponseEntity.status(HttpStatus.OK).body(response); 
  }


  @DeleteMapping("/delete-account")
  public ResponseEntity<MessageResponseDTO> deleteAccount(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse httpResponse) {
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    userService.deleteByEmail(userDetails.getUsername());

    ResponseCookie cookie = ResponseCookie.from("jwt_token", "")
      .httpOnly(true)
      .secure(true)
      .sameSite("None")
      .path("/")
      .maxAge(0)
      .build();
    httpResponse.addHeader("Set-Cookie", cookie.toString());

    MessageResponseDTO response = new MessageResponseDTO("Аккаунт успешно удален");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }


}
