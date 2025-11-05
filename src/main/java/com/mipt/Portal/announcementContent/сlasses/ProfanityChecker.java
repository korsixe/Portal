package com.mipt.Portal.announcementContent.сlasses;

import java.net.*;
import java.net.http.*;
import java.time.Duration;

public class ProfanityChecker {
  private final HttpClient client = HttpClient.newHttpClient();

  public boolean containsProfanity(String text) {
    try {
      if (text == null || text.trim().isEmpty()) return false;

      String encodedText = URLEncoder.encode(text, "UTF-8");
      String url = "https://www.purgomalum.com/service/containsprofanity?text=" + encodedText;

      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(3)).GET().build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      return Boolean.parseBoolean(response.body().trim());

    } catch (Exception e) {
      return false;
    }
  }


}