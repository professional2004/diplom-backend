package com.textilecad.controllers;

import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorControllerImpl implements ErrorController{
  @RequestMapping("/error")
  public String handleError() {
    return "forward:/error.html"; 
  }
}
