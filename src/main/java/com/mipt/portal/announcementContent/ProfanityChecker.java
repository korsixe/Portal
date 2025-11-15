package com.mipt.portal.announcementContent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ProfanityChecker {
  private static final Set<String> PROFANITY_WORDS = new HashSet<>(Arrays.asList(

      "мат", "брань", "ругательство", "плохоеслово",

      "damn", "hell", "crap", "bitch", "ass", "shit", "fuck", "bastard", "piss", "dick", "cock", "pussy"
  ));

  public boolean containsProfanity(String text) {
    if (text == null || text.trim().isEmpty()) {
      return false;
    }

    String lowerText = text.toLowerCase();
    String cleanText = lowerText.replaceAll("[^\\p{L}\\p{N}\\s]", " ");
    String[] words = cleanText.split("\\s+");
    
    for (String word : words) {
      if (word.length() > 2 && PROFANITY_WORDS.contains(word)) {
        return true;
      }
    }
    
    return false;
  }
}
