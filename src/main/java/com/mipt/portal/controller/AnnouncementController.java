package com.mipt.portal.controller;

import com.mipt.portal.announcement.Announcement;
import com.mipt.portal.announcement.AdsService;
import com.mipt.portal.announcement.Category;
import com.mipt.portal.announcement.Condition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AnnouncementController {

  private final AdsService announcementService;

  // Страница создания объявления
  @GetMapping("/new")
  public String createAdForm(Model model) {
    model.addAttribute("ad", new Announcement());
    model.addAttribute("categories", Category.values());
    model.addAttribute("conditions", Condition.values());
    model.addAttribute("title", "Создать объявление");
    return "ads/create";
  }

  // to be continued..........
}
