package com.textilecad.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class NavigationController {
  @GetMapping("/")
  public String getMainpage() {
    return "forward:/main.html";
  }

  @GetMapping("/app")
  public String getAppPage() {
    return "forward:/home.html";
  }


  @GetMapping("/login")
  public String getLoginPage() {
    return "forward:/signin.html";
  }


  @GetMapping("/register")
  public String getRegistrationPage() {
    return "forward:/signup.html";
  }
  
}
