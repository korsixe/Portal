package com.mipt.portal.moderator;

import com.mipt.portal.announcement.AdsService;

import java.util.*;

public class ModeratorService {

  private final AdsService adsService;
  private final ModeratorRepository moderatorRepository;
  private Moderator moderator;

  public ModeratorService(AdsService adsService, ModeratorRepository moderatorRepository) {
    this.adsService = adsService;
    this.moderatorRepository = moderatorRepository;
  }

  public boolean login(String email, String password) {
    Optional<Moderator> moderatorOpt = moderatorRepository.findByEmail(email);
    if (moderatorOpt.isPresent() && moderatorOpt.get().getPassword().equals(password)) {
      moderator = moderatorOpt.get();
      return true;
    }
    return false;
  }
}
