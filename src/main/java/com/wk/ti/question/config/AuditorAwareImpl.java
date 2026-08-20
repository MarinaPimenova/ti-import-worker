package com.wk.ti.question.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@SuppressWarnings("NullableProblems")
@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {

    private static final String SYSTEM_USER = "system";

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("No authenticated user found. Using {}", SYSTEM_USER);
            return Optional.of(SYSTEM_USER);
        }

        String username = authentication.getName();

        if (username == null || username.isBlank()) {
            log.debug("Authenticated user has no username. Using {}", SYSTEM_USER);
            return Optional.of(SYSTEM_USER);
        }

        String auditor = username.toLowerCase();

        log.debug("Actions are performed on behalf of {}", auditor);

        return Optional.of(auditor);
    }

}
