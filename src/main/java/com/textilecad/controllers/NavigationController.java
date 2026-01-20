package com.textilecad.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class NavigationController {
  @GetMapping("/")
  public String getMainpage() {
    return "forward:/index.html";
  }
}
