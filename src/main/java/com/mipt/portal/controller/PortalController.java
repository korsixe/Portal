package com.mipt.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class PortalController {

  @GetMapping
  public String home(Model model) {
    model.addAttribute("message", "Добро пожаловать в Portal!");
    model.addAttribute("title", "Главная страница");
    return "index";
  }

  @GetMapping("/ads")
  public String adsPage(Model model) {
    model.addAttribute("title", "Объявления");
    return "ads";
  }

  @GetMapping("/create-ad")
  public String createAdPage(Model model) {
    model.addAttribute("title", "Создать объявление");
    return "create-ad";
  }
}