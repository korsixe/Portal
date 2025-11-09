package com.mipt.portal.moderator;

import java.util.Optional;
import java.util.regex.Pattern;

public class ModeratorRegistrationImpl implements ModeratorRegistration {
    private final ModeratorRepository moderatorRepository;

    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!?@#$%&*_\\-]).{8,}$";

    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9._%+-]+@(phystech\\.edu|mipt\\.ru)$";

    public ModeratorRegistrationImpl(ModeratorRepository moderatorRepository) {
        this.moderatorRepository = moderatorRepository;
    }

    @Override
    public Optional<Moderator> register(Moderator moderator) {
        if (!validateEmail(moderator.getEmail())) {
            System.out.println("Неверный формат email!");
            return Optional.empty();
        }

        if (!validatePassword(moderator.getPassword())) {
            System.out.println("Пароль не соответствует требованиям!");
            return Optional.empty();
        }

        Optional<Moderator> existingModerator = moderatorRepository.findByEmail(moderator.getEmail());

        if (existingModerator.isPresent()) {
            System.out.println("Модератор с таким email уже зарегистрирован");
            return Optional.empty();
        }

        return moderatorRepository.save(moderator);
    }

    @Override
    public boolean validatePassword(String password) {
        return Pattern.matches(PASSWORD_PATTERN, password);
    }

    @Override
    public boolean validateEmail(String email) {
        return Pattern.matches(EMAIL_PATTERN, email);
    }
}
