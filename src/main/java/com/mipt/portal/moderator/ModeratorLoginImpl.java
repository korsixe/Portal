package com.mipt.portal.moderator;

import java.util.Optional;

public class ModeratorLoginImpl implements ModeratorLogin {
    private final ModeratorRepository moderatorRepository;

    public ModeratorLoginImpl(ModeratorRepository moderatorRepository) {
        this.moderatorRepository = moderatorRepository;
    }

    @Override
    public Moderator login(String email, String password) {
        Optional<Moderator> moderatorOpt = moderatorRepository.findByEmail(email);

        if (moderatorOpt.isPresent()) {
            Moderator moderator = moderatorOpt.get();

            if (moderator.getPassword().equals(password)) {
                return moderator;
            }
        }
        return null;
    }
}
