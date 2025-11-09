package com.mipt.portal.moderator;

import java.util.Optional;

public interface ModeratorRegistration {
    Optional<Moderator> register(Moderator moderator);
    boolean validatePassword(String password);
    boolean validateEmail(String email);
}
