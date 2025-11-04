package com.mipt.portal.controller;

import com.mipt.portal.announcement.AdsService;
import com.mipt.portal.announcement.Announcement;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/api/ads")
public class AdsController {

  @Autowired
  private AdsService adsService;

  @PostMapping("/create")
  public String createAd(@RequestParam Long userId,
      @RequestParam String title,
      @RequestParam String description,
      Model model) {
    try {
      // Здесь будет логика создания объявления через веб-форму
      // Пока заглушка
      model.addAttribute("message", "Объявление создано успешно!");
      return "redirect:/ads";
    } catch (Exception e) {
      model.addAttribute("error", "Ошибка при создании объявления: " + e.getMessage());
      return "create-ad";
    }
  }

  @GetMapping("/{id}")
  public String getAd(@PathVariable Long id, Model model) {
    try {
      Announcement ad = adsService.getAd(id);
      if (ad != null) {
        model.addAttribute("ad", ad);
        return "ad-details";
      } else {
        model.addAttribute("error", "Объявление не найдено");
        return "error";
      }
    } catch (Exception e) {
      model.addAttribute("error", "Ошибка: " + e.getMessage());
      return "error";
    }
  }
}